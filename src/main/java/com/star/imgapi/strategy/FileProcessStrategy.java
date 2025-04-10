package com.star.imgapi.strategy;

import com.star.imgapi.entity.UploadResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理策略接口
 */
public interface FileProcessStrategy {
    UploadResult process(MultipartFile file, String fileName, String chunkIndex);

    boolean supports(String fileExtension);
}