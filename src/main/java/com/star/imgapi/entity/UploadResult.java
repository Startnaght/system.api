package com.star.imgapi.entity;


import com.star.imgapi.util.FileTypeUtil;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 优化的上传结果类
 */
@Data
public class UploadResult {
    private boolean success;
    private String message;
    private String filePath;
    private String fileName;
    private String fileType;
    private String fileCategory;
    private long fileSize;
    private String downloadUrl;
    private LocalDateTime uploadTime;

    public UploadResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.uploadTime = LocalDateTime.now();
    }

    public UploadResult(boolean success, String message, String filePath,
                        String fileName, String fileType, long fileSize) {
        this(success, message);
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileCategory = FileTypeUtil.getFileCategory(fileType);
    }

    // 静态工厂方法
    public static UploadResult success(String message) {
        return new UploadResult(true, message);
    }

    public static UploadResult failure(String message) {
        return new UploadResult(false, message);
    }

    public static UploadResult of(boolean success, String message, String filePath,
                                  String fileName, String fileType, long fileSize) {
        return new UploadResult(success, message, filePath, fileName, fileType, fileSize);
    }
}