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
 * 文档文件处理器
 */
@Component
public class DocumentFileProcessor implements FileProcessStrategy {
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"
    );

    @Override
    public UploadResult process(MultipartFile file, String fileName, String chunkIndex) {
        try {
            String filePath = "F:\\changan\\system\\documents\\" + fileName;
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // 文档文件特殊处理，比如病毒扫描、元数据提取等
            extractDocumentMetadata(filePath);

            return new UploadResult(true, "文档上传成功", filePath, fileName,
                    getFileExtension(fileName), file.getSize());
        } catch (Exception e) {
            GlobalLog.error("文档处理失败: " + e.getMessage());
            return new UploadResult(false, "文档处理失败: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }

    private void extractDocumentMetadata(String filePath) {
        // 提取文档元数据
        GlobalLog.info("提取文档元数据: " + filePath);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}