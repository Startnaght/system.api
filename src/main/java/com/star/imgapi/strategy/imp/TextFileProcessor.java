package com.star.imgapi.strategy.imp;

import com.star.imgapi.entity.UploadResult;
import com.star.imgapi.strategy.FileProcessStrategy;
import com.star.imgapi.util.GlobalLog;
import com.star.imgapi.util.UtilOsStarem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;

/**
 * 文本文件处理器
 */
@Component
public class TextFileProcessor implements FileProcessStrategy {
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList("txt", "log", "ini", "conf");

    @Autowired
    private UtilOsStarem utilOsStarem;

    @Override
    public UploadResult process(MultipartFile file, String fileName, String chunkIndex) {
        try {
            String filePath = "F:\\changan\\system\\documents\\" + fileName;

            // 使用现有的文本处理逻辑
            BufferedReader reader = utilOsStarem.readChunks(file, filePath);
            utilOsStarem.os_Save(reader, filePath);

            // 可以添加文本文件的特殊处理，比如内容验证、编码检测等
            validateTextContent(filePath);

            return new UploadResult(true, "文本文件上传成功", filePath, fileName,
                    "txt", file.getSize());
        } catch (Exception e) {
            GlobalLog.error("文本文件处理失败: " + e.getMessage());
            return new UploadResult(false, "文本文件处理失败: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }

    private void validateTextContent(String filePath) {
        // 文本内容验证逻辑
        GlobalLog.info("验证文本内容: " + filePath);
    }
}