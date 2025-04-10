package com.star.imgapi.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    // 允许上传的文件类型白名单
    private List<String> allowedTypes = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", // 图片
            "txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", // 文档
            "zip", "rar", "7z", // 压缩包
            "mp4", "avi", "mov", "mkv", // 视频
            "mp3", "wav", "flac" // 音频
    );

    // 单个文件大小限制（MB）
    private long maxFileSize = 50;

    // 总上传大小限制（MB）
    private long maxRequestSize = 100;

    // 文件存储基础路径
    private String basePath = "F:\\changan\\system\\uploads\\";

    // 不同类型文件的存储子目录
    private String imagePath = "images/";
    private String documentPath = "documents/";
    private String videoPath = "videos/";
    private String audioPath = "audios/";
    private String archivePath = "archives/";
    private String otherPath = "others/";

}