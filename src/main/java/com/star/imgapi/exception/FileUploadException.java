package com.star.imgapi.exception;

import com.star.imgapi.util.Code;
import lombok.Getter;

/**
 * 文件上传异常
 */
@Getter
public class FileUploadException extends BusinessException {
    private final String fileName;
    private final String fileType;

    public FileUploadException(Code errorCode, String message) {
        super(errorCode, message);
        this.fileName = null;
        this.fileType = null;
    }

    public FileUploadException(Code errorCode, String message, String fileName, String fileType) {
        super(errorCode, message);
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public FileUploadException(Code errorCode, String message, String fileName, String fileType, Throwable cause) {
        super(errorCode, message, cause);
        this.fileName = fileName;
        this.fileType = fileType;
    }
}