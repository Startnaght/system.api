package com.star.imgapi.dto.request;

import com.star.imgapi.validation.annotation.ValidFileType;
import com.star.imgapi.validation.annotation.ValidFileName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文件上传请求DTO
 */
@Data
public class FileUploadRequest {

    @NotNull(message = "文件不能为空")
    @ValidFileType(
            allowedTypes = {
                    "jpg", "jpeg", "png", "gif", "bmp", "webp",
                    "txt", "pdf", "doc", "docx", "xls", "xlsx",
                    "ppt", "pptx", "zip", "rar", "7z",
                    "mp4", "avi", "mov", "mkv", "mp3", "wav", "flac"
            },
            maxSize = 50,
            message = "文件类型不支持或大小超过限制"
    )
    private MultipartFile file;

    @NotBlank(message = "文件名不能为空")
    @ValidFileName(message = "文件名格式不正确")
    private String fileName;

    private String chunkIndex = "0";
    private String description;
}