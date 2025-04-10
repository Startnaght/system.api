package com.star.imgapi.exception;

import com.star.imgapi.util.Code;
import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Code errorCode;
    private final String detailMessage;

    public BusinessException(Code errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.detailMessage = errorCode.name();
    }

    public BusinessException(Code errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.detailMessage = message;
    }

    public BusinessException(Code errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.detailMessage = message;
    }

    public int getCodeValue() {
        return errorCode.getCode();
    }
}