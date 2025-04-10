package com.star.imgapi.exception;

import com.star.imgapi.util.Code;
import lombok.Getter;

/**
 * API调用异常
 */
@Getter
public class ApiCallException extends BusinessException {
    private final String apiUrl;
    private final String httpMethod;
    private final int httpStatus;

    public ApiCallException(Code errorCode, String message, String apiUrl, String httpMethod) {
        super(errorCode, message);
        this.apiUrl = apiUrl;
        this.httpMethod = httpMethod;
        this.httpStatus = -1;
    }

    public ApiCallException(Code errorCode, String message, String apiUrl, String httpMethod, int httpStatus) {
        super(errorCode, message);
        this.apiUrl = apiUrl;
        this.httpMethod = httpMethod;
        this.httpStatus = httpStatus;
    }
}