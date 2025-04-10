package com.star.imgapi.service.impl;

import com.star.imgapi.entity.UploadResult;
import com.star.imgapi.factory.FileProcessorFactory;
import com.star.imgapi.strategy.FileProcessStrategy;
import com.star.imgapi.service.UploadService;
import com.star.imgapi.util.FileTypeUtil;
import com.star.imgapi.util.GlobalLog;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 优化后的上传服务实现
 */
@Service
public class OptimizedUploadService implements UploadService {

    @Autowired
    private FileProcessorFactory processorFactory;

    @Override
    public UploadResult uploadFile(MultipartFile file, String fileName, String chunkIndex) {
        try {
            // 参数验证
            if (file == null || file.isEmpty()) {
                return UploadResult.failure("文件不能为空");
            }

            // 获取文件扩展名
            String fileExtension = FileTypeUtil.getFileExtension(fileName);
            if (fileExtension.isEmpty()) {
                return UploadResult.failure("无法识别的文件类型");
            }

            // 验证文件类型和大小
            UploadResult validationResult = validateFile(file, fileExtension);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }

            // 生成唯一文件名
            String uniqueFileName = generateUniqueFileName(fileName, fileExtension);

            // 使用策略模式处理文件
            FileProcessStrategy processor = processorFactory.getProcessor(fileExtension);
            if (processor != null) {
                return processor.process(file, uniqueFileName, chunkIndex);
            } else {
                // 使用默认处理器
                return processWithDefaultStrategy(file, uniqueFileName, fileExtension);
            }

        } catch (Exception e) {
            GlobalLog.error("文件上传异常: " + e.getMessage());
            return UploadResult.failure("文件上传异常: " + e.getMessage());
        }
    }

    /**
     * 文件验证
     */
    private UploadResult validateFile(MultipartFile file, String fileExtension) {
        // 文件大小验证
        if (file.getSize() > 50 * 1024 * 1024) { // 50MB
            return UploadResult.failure("文件大小不能超过50MB");
        }

        // 文件类型验证
        if (!FileTypeUtil.isAllowedType(fileExtension)) {
            return UploadResult.failure("不支持的文件类型: " + fileExtension);
        }

        return UploadResult.success("验证通过");
    }

    /**
     * 生成唯一文件名
     */
    private String generateUniqueFileName(String originalName, String extension) {
        String nameWithoutExt = originalName.substring(0, originalName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return nameWithoutExt + "_" + uuid + "." + extension;
    }

    /**
     * 默认处理策略
     */
    private UploadResult processWithDefaultStrategy(MultipartFile file, String fileName, String fileExtension) {
        try {
            String filePath = "F:\\changan\\system\\others\\" + fileName;
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filePath),
                    file.getBytes()
            );

            return new UploadResult(true, "文件上传成功", filePath, fileName,
                    fileExtension, file.getSize());
        } catch (Exception e) {
            return UploadResult.failure("文件保存失败: " + e.getMessage());
        }
    }

    // 其他方法实现...
    @Override
    public void saveImage(MultipartFile file, String fileName) {
        // 委托给图片处理器
        FileProcessStrategy processor = processorFactory.getProcessor("jpg");
        processor.process(file, fileName, "0");
    }

    @Override
    public void saveText(MultipartFile file, String fileName, String chunkIndex) {
        FileProcessStrategy processor = processorFactory.getProcessor("txt");
        processor.process(file, fileName, chunkIndex);
    }

    @Override
    public void saveDocument(MultipartFile file, String fileName) {
        FileProcessStrategy processor = processorFactory.getProcessor("pdf");
        processor.process(file, fileName, "0");
    }

    @Override
    public void saveVideo(MultipartFile file, String fileName) {
        FileProcessStrategy processor = processorFactory.getProcessor("mp4");
        processor.process(file, fileName, "0");
    }

    @Override
    public void saveAudio(MultipartFile file, String fileName) {
        FileProcessStrategy processor = processorFactory.getProcessor("mp3");
        processor.process(file, fileName, "0");
    }

    @Override
    public void saveArchive(MultipartFile file, String fileName) {
        FileProcessStrategy processor = processorFactory.getProcessor("zip");
        processor.process(file, fileName, "0");
    }

    @Override
    public void saveOther(MultipartFile file, String fileName) {
        processWithDefaultStrategy(file, fileName,
                FileTypeUtil.getFileExtension(fileName));
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            return java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(filePath));
        } catch (Exception e) {
            GlobalLog.error("删除文件失败: " + e.getMessage());
            return false;
        }
    }
/// 暂时未实现
    @Override
    public FileInfo getFileInfo(String fileName) {
        // 实现获取文件信息逻辑
        return null;
    }
}