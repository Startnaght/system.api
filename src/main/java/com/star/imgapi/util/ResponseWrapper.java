package com.star.imgapi.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 修复的响应包装器 - 支持错误处理
 */
@Data
public class ResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;
    private String code;
    private long timestamp;

    public ResponseWrapper() {
        this.timestamp = System.currentTimeMillis();
    }

    // 成功构造器
    public ResponseWrapper(T data) {
        this.success = true;
        this.message = "操作成功";
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.code = "SUCCESS";
    }

    public ResponseWrapper(T data, String message) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.code = "SUCCESS";
    }

    // 错误构造器
    public ResponseWrapper(String errorMessage) {
        this.success = false;
        this.message = errorMessage;
        this.timestamp = System.currentTimeMillis();
        this.code = "ERROR";
    }

    // 带错误码的构造器
    public ResponseWrapper(String errorMessage, String errorCode) {
        this.success = false;
        this.message = errorMessage;
        this.code = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    // 静态工厂方法
    public static <T> ResponseWrapper<T> success(T data) {
        return new ResponseWrapper<>(data);
    }

    public static <T> ResponseWrapper<T> success(T data, String message) {
        return new ResponseWrapper<>(data, message);
    }

    public static <T> ResponseWrapper<T> error(String message) {
        return new ResponseWrapper<>(message);
    }

    public static <T> ResponseWrapper<T> error(String message, String errorCode) {
        return new ResponseWrapper<>(message, errorCode);
    }

    // 创建标准错误响应
    public static <T> ResponseWrapper<Map<String, Object>> createErrorResponse(String message, Map<String, Object> errorDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        response.put("code", "ERROR");

        if (errorDetails != null) {
            response.putAll(errorDetails);
        }

        return new ResponseWrapper<>(response);
    }


}