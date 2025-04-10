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

import static com.star.imgapi.util.FileTypeUtil.getFileExtension;

/**
 * 图片文件处理器
 */
@Component
public class ImageFileProcessor implements FileProcessStrategy {
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    @Override
    public UploadResult process(MultipartFile file, String fileName, String chunkIndex) {
        try {
            // 图片特殊处理逻辑，比如压缩、生成缩略图等
            String filePath = "F:\\changan\\system\\images\\" + fileName;
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // 可以在这里添加图片处理逻辑，比如生成缩略图
            generateThumbnail(file, filePath);

            return new UploadResult(true, "图片上传成功", filePath, fileName,
                    getFileExtension(fileName), file.getSize());
        } catch (Exception e) {
            GlobalLog.error("图片处理失败: " + e.getMessage());
            return new UploadResult(false, "图片处理失败: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }

    private void generateThumbnail(MultipartFile file, String filePath) {
        // 实现缩略图生成逻辑
        GlobalLog.info("生成缩略图: " + filePath);
    }
}