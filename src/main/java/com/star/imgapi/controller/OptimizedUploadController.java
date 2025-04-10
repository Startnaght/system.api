package com.star.imgapi.controller;

import com.star.imgapi.entity.UploadResult;
import com.star.imgapi.service.UploadService;
import com.star.imgapi.util.ReStruct;
import com.star.imgapi.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 优化的文件上传控制器
 */
@RestController
@RequestMapping("/api/v2")
public class OptimizedUploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    public ResponseWrapper uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "chunkIndex", defaultValue = "0") String chunkIndex,
            @RequestParam("fileName") String fileName) {

        UploadResult result = uploadService.uploadFile(file, fileName, chunkIndex);

        return buildResponse(result);
    }

    /**
     * 批量文件上传
     */
    @PostMapping("/upload/batch")
    public ResponseWrapper uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "chunkIndex", defaultValue = "0") String chunkIndex) {

        List<UploadResult> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadService.uploadFile(file, file.getOriginalFilename(), chunkIndex));
        }

        boolean allSuccess = results.stream().allMatch(UploadResult::isSuccess);
        String message = allSuccess ? "所有文件上传成功" : "部分文件上传失败";

        return new ResponseWrapper<>(new ReStruct().ReJsonData(results, message));
    }

    /**
     * 构建统一响应
     */
    private ResponseWrapper buildResponse(UploadResult result) {
        if (result.isSuccess()) {
            return new ResponseWrapper<>(new ReStruct().ReJsonSuccess(new ArrayList<>(
                    List.of(result.getMessage(),
                            result.getFileName(),
                            result.getFilePath(),
                            String.valueOf(result.getFileSize()),
                            result.getFileCategory()
                    ))));
        } else {
            return new ResponseWrapper<>(new ReStruct().ReJsonErr(new ArrayList<>(
                    List.of(result.getMessage()))));
        }
    }

    /**
     * 获取上传进度（用于大文件分片上传）
     */
    @GetMapping("/upload/progress/{fileId}")
    public ResponseWrapper getUploadProgress(@PathVariable String fileId) {
        // 实现上传进度查询
        return new ResponseWrapper<>(new ReStruct().ReJsonData(
                new UploadProgress(fileId, 0.5f), "上传进度查询成功"));
    }

    // 进度信息内部类
    private static class UploadProgress {
        private String fileId;
        private float progress;

        public UploadProgress(String fileId, float progress) {
            this.fileId = fileId;
            this.progress = progress;
        }

        // getter方法
        public String getFileId() { return fileId; }
        public float getProgress() { return progress; }
    }
}