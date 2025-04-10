package com.star.imgapi.strategy.imp;

import com.star.imgapi.entity.UploadResult;
import com.star.imgapi.strategy.FileProcessStrategy;
import com.star.imgapi.util.GlobalLog;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 媒体文件处理器（视频、音频）
 */
@Component
public class MediaFileProcessor implements FileProcessStrategy {
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "mp4", "avi", "mov", "mkv", "mp3", "wav", "flac"
    );

    @Override
    public UploadResult process(MultipartFile file, String fileName, String chunkIndex) {
        try {
            String fileExtension = getFileExtension(fileName);
            String category = isVideo(fileExtension) ? "videos" : "audios";
            String filePath = "F:\\changan\\system\\" + category + "\\" + fileName;

            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // 媒体文件特殊处理，比如生成预览、提取元数据等
            processMediaFile(filePath, fileExtension);

            return new UploadResult(true, "媒体文件上传成功", filePath, fileName,
                    fileExtension, file.getSize());
        } catch (Exception e) {
            GlobalLog.error("媒体文件处理失败: " + e.getMessage());
            return new UploadResult(false, "媒体文件处理失败: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }

    private boolean isVideo(String fileExtension) {
        return Arrays.asList("mp4", "avi", "mov", "mkv").contains(fileExtension.toLowerCase());
    }

    private void processMediaFile(String filePath, String fileExtension) {
        if (isVideo(fileExtension)) {
            generateVideoPreview(filePath);
        } else {
            extractAudioMetadata(filePath);
        }
    }

    private void generateVideoPreview(String filePath) {
        // 生成视频预览
        GlobalLog.info("生成视频预览: " + filePath);
    }

    private void extractAudioMetadata(String filePath) {
        // 提取音频元数据
        GlobalLog.info("提取音频元数据: " + filePath);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}