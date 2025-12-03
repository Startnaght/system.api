package com.star.imgapi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.star.imgapi.config.webClientConfig;
import com.star.imgapi.entity.Uploadteam;
import com.star.imgapi.entity.hitokotoCode;
import com.star.imgapi.service.HitokotoService;
import com.star.imgapi.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.star.imgapi.service.impl.uploadserverimpl;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: oyster
 * @Description: 适配star_bigdata数据库的控制器
 * @DateTime: 2022/6/6 18:47
 **/
@RestController
@RequestMapping("/api")
public class CompleteController {

    @Autowired
    private webClientConfig webClientConfig;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private uploadserverimpl uploadserveri;

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    private HitokotoService hitokotoService;

    /**
     * 健康检查接口
     */
    @PostMapping("/text")
    public ResponseWrapper<String> text() {
        // 记录操作日志
        databaseUtil.insertOperationLog("health_check", "system",
                "健康检查", "POST", "/api/text", getClientIp(request));

        return new ResponseWrapper<>("star_bigdata 服务运行正常");
    }

    /**
     * 获取一言接口 - 使用服务类
     */
    @PostMapping("/yi")
    public ResponseWrapper<Map<String, Object>> getHitokoto(@RequestBody(required = false) Uploadteam uploadteam) {
        System.out.println(uploadteam+"\n");
        try {
            // 处理空请求体的情况
            if (uploadteam == null) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "请求体不能为空");
                errorResult.put("suggestion", "请提供有效的JSON请求体，例如: {\"name\":\"b\",\"index\":0}");
                return ResponseWrapper.error(errorResult.toString());
            }

            String category = (uploadteam.getName() != null && !uploadteam.getName().isEmpty())
                    ? uploadteam.getName() : "b";
            System.out.println(category);
            GlobalLog.info("获取一言请求，分类: " + category);

            // 使用服务类获取一言
            Mono<hitokotoCode> hitokotoMono = hitokotoService.getHitokotoWithFallback(category);
            hitokotoCode hitokoto = hitokotoMono.block();

            if (hitokoto != null) {
                // 构建响应
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("hitokoto", hitokoto.getHitokoto());
                result.put("from", hitokoto.getFrom());
                result.put("type", hitokoto.getType());
                result.put("message", "获取成功");

                // 记录到数据库（如果数据库可用）
                try {
                    databaseUtil.insertHitokotoData(hitokoto, getClientIp(request), category);
                } catch (Exception e) {
                    GlobalLog.warn("数据库记录失败: " + e.getMessage());
                }

                return ResponseWrapper.success(result);
            } else {
                return ResponseWrapper.error("获取一言失败");
            }

        } catch (Exception e) {
            GlobalLog.error("获取一言异常: " + e.getMessage());
            return ResponseWrapper.error("一言服务异常: " + e.getMessage());
        }
    }


    /**
     * 单文件上传 - 适配新数据库结构
     */
    @PostMapping("/upload")
    public ResponseWrapper<Map<String, Object>> upload(@RequestParam("file") MultipartFile data,
                                                       @RequestParam("chunkIndex") String chunkIndex,
                                                       @RequestParam("fileName") String fileName) {
        long startTime = System.currentTimeMillis();
        String clientIp = getClientIp(request);

        try {
            // 记录上传开始
            databaseUtil.insertOperationLog("file_upload", "upload",
                    "上传文件: " + fileName, "POST", "/api/upload", clientIp);

            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String storedFileName = uuid + suffixName;

            Map<String, Object> result = new HashMap<>();

            // 文件类型处理
            if (suffixName.equals(".txt")) {
                uploadserveri.SaveTxt(data, storedFileName, chunkIndex);
                result.put("message", "文本文件上传成功");
            } else if (suffixName.equals(".jpg") || suffixName.equals(".png")) {
                uploadserveri.SaveImg(data, storedFileName);
                result.put("message", "图片文件上传成功");
            } else {
                result.put("success", false);
                result.put("message", "上传失败，服务器暂不支持此文件类型上传~");
                return new ResponseWrapper<>(result);
            }

            // 插入文件上传记录到新数据库
            String filePath = "F:/changan/system/uploads/" + storedFileName;
            String fileType = getFileType(suffixName);
            databaseUtil.insertFileUploadRecord(fileName, storedFileName, filePath,
                    data.getSize(), fileType, clientIp, chunkIndex, "user");

            long responseTime = System.currentTimeMillis() - startTime;
            databaseUtil.updateApiStatistics("/api/upload", "POST", (int)responseTime, true, clientIp);

            // 构建成功响应
            result.put("success", true);
            result.put("welcomeMessage", "长安管理平台开发者-->> 欢迎你的使用~~");
            result.put("storedName", storedFileName);
            result.put("originalName", fileName);
            result.put("fileSize", data.getSize());
            result.put("fileType", fileType);
            result.put("responseTime", responseTime);
            result.put("requestUri", request.getRequestURI());

            return new ResponseWrapper<>(result);

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            databaseUtil.updateApiStatistics("/api/upload", "POST", (int)responseTime, false, clientIp);

            GlobalLog.error("文件上传异常: " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "文件上传异常: " + e.getMessage());
            return new ResponseWrapper<>(errorResult);
        }
    }

    /**
     * 批量文件上传接口 - 适配新数据库结构
     */
    @PostMapping("/upload/batch")
    public ResponseWrapper<Map<String, Object>> uploadBatchFiles(@RequestParam("files") MultipartFile[] files) {
        long startTime = System.currentTimeMillis();
        String clientIp = getClientIp(request);

        try {
            // 记录批量上传开始
            databaseUtil.insertOperationLog("batch_upload", "upload",
                    "批量上传文件，数量: " + (files != null ? files.length : 0),
                    "POST", "/api/upload/batch", clientIp);

            if (files == null || files.length == 0) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "没有选择文件");
                return new ResponseWrapper<>(errorResult);
            }

            List<Map<String, Object>> results = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (MultipartFile file : files) {
                Map<String, Object> fileResult = new HashMap<>();
                try {
                    String fileName = file.getOriginalFilename();
                    if (fileName == null) {
                        failCount++;
                        fileResult.put("status", "失败");
                        fileResult.put("message", "文件名不能为空");
                        results.add(fileResult);
                        continue;
                    }

                    String suffixName = fileName.substring(fileName.lastIndexOf("."));
                    String uuid = UUID.randomUUID().toString().replace("-", "");
                    String storedFileName = uuid + suffixName;

                    if (suffixName.equals(".txt")) {
                        uploadserveri.SaveTxt(file, storedFileName, "0");
                        successCount++;
                        fileResult.put("status", "成功");
                    } else if (suffixName.equals(".jpg") || suffixName.equals(".png")) {
                        uploadserveri.SaveImg(file, storedFileName);
                        successCount++;
                        fileResult.put("status", "成功");
                    } else {
                        failCount++;
                        fileResult.put("status", "失败");
                        fileResult.put("message", "不支持的文件类型: " + suffixName);
                        results.add(fileResult);
                        continue;
                    }

                    // 插入单个文件记录
                    String filePath = "F:/changan/system/uploads/" + storedFileName;
                    String fileType = getFileType(suffixName);
                    databaseUtil.insertFileUploadRecord(fileName, storedFileName, filePath,
                            file.getSize(), fileType, clientIp, "0", "user");

                    fileResult.put("fileName", fileName);
                    fileResult.put("storedName", storedFileName);
                    fileResult.put("size", file.getSize());
                    fileResult.put("fileType", fileType);
                    results.add(fileResult);

                } catch (Exception e) {
                    failCount++;
                    fileResult.put("fileName", file.getOriginalFilename());
                    fileResult.put("status", "失败");
                    fileResult.put("message", e.getMessage());
                    results.add(fileResult);
                }
            }

            long responseTime = System.currentTimeMillis() - startTime;
            databaseUtil.updateApiStatistics("/api/upload/batch", "POST", (int)responseTime, true, clientIp);

            Map<String, Object> batchResult = new HashMap<>();
            batchResult.put("success", true);
            batchResult.put("total", files.length);
            batchResult.put("successCount", successCount);
            batchResult.put("failCount", failCount);
            batchResult.put("results", results);
            batchResult.put("responseTime", responseTime);
            batchResult.put("message", String.format("批量上传完成，成功%d个，失败%d个", successCount, failCount));

            return new ResponseWrapper<>(batchResult);

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            databaseUtil.updateApiStatistics("/api/upload/batch", "POST", (int)responseTime, false, clientIp);

            GlobalLog.error("批量上传异常: " + e.getMessage());
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "批量上传异常: " + e.getMessage());
            return new ResponseWrapper<>(errorResult);
        }
    }

    /**
     * 快速获取一言（GET方式）
     */
    @GetMapping("/yiyan/quick")
    public ResponseWrapper<Map<String, Object>> getQuickHitokoto(
            @RequestParam(defaultValue = "b") String category) {

        try {
            GlobalLog.info("快速获取一言，分类: " + category);

            Mono<hitokotoCode> hitokotoMono = hitokotoService.getHitokotoWithFallback(category);
            hitokotoCode hitokoto = hitokotoMono.block();

            if (hitokoto != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("hitokoto", hitokoto.getHitokoto());
                result.put("from", hitokoto.getFrom());
                result.put("type", hitokoto.getType());
                result.put("category", category);

                return ResponseWrapper.success(result);
            } else {
                return ResponseWrapper.error("获取一言失败");
            }

        } catch (Exception e) {
            GlobalLog.error("快速获取一言异常: " + e.getMessage());
            return ResponseWrapper.error("服务异常: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseWrapper<Map<String, Object>> healthCheck() {
        // 记录健康检查
        databaseUtil.insertOperationLog("health_check", "system",
                "健康检查", "GET", "/api/health", getClientIp(request));

        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "Star BigData API");
        healthInfo.put("database", "star_bigdata");
        healthInfo.put("timestamp", System.currentTimeMillis());
        healthInfo.put("version", "1.0.0");

        return new ResponseWrapper<>(healthInfo);
    }

    /**
     * 服务器信息接口
     */
    @GetMapping("/info")
    public ResponseWrapper<Map<String, Object>> serverInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Star BigData API");
        info.put("description", "基于star_bigdata数据库的文件上传和一言API服务");
        info.put("database", "star_bigdata");
        info.put("uploadSupported", new String[]{"txt", "jpg", "jpeg", "png", "gif", "bmp", "pdf"});
        info.put("hitokotoSupported", new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"});
        info.put("features", new String[]{
                "文件上传记录", "一言API记录", "操作日志", "API访问统计", "系统配置管理"
        });

        return new ResponseWrapper<>(info);
    }

    /**
     * 新增：获取文件统计信息
     */
    @GetMapping("/stats/files")
    public ResponseWrapper<Map<String, Object>> getFileStatistics() {
        try {
            // 这里可以添加从数据库获取文件统计的逻辑
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalFiles", 0); // 从数据库查询
            stats.put("totalSize", 0);
            stats.put("fileTypes", new String[]{"jpg", "png", "txt"});
            stats.put("lastUploadTime", System.currentTimeMillis());

            return new ResponseWrapper<>(stats, "文件统计获取成功");
        } catch (Exception e) {
            return new ResponseWrapper<>("获取文件统计失败: " + e.getMessage());
        }
    }

    /**
     * 新增：获取一言统计信息
     */
    @GetMapping("/stats/hitokoto")
    public ResponseWrapper<Map<String, Object>> getHitokotoStatistics() {
        try {
            // 这里可以添加从数据库获取一言统计的逻辑
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRequests", 0); // 从数据库查询
            stats.put("successRate", "100%");
            stats.put("popularCategories", new String[]{"b", "a", "c"});
            stats.put("lastRequestTime", System.currentTimeMillis());

            return new ResponseWrapper<>(stats, "一言统计获取成功");
        } catch (Exception e) {
            return new ResponseWrapper<>("获取一言统计失败: " + e.getMessage());
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 根据文件扩展名获取文件类型
     */
    private String getFileType(String fileExtension) {
        if (fileExtension == null) return "other";

        switch (fileExtension.toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return "image";
            case "txt":
            case "pdf":
            case "doc":
            case "docx":
                return "document";
            case "mp4":
            case "avi":
            case "mov":
                return "video";
            case "mp3":
            case "wav":
            case "flac":
                return "audio";
            case "zip":
            case "rar":
            case "7z":
                return "archive";
            default:
                return "other";
        }
    }
}