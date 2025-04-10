package com.star.imgapi.util;

/**
 * 修复的 Code 枚举 - 添加缺失的异常类型
 */
public enum Code {
    // 系统状态码
    SUCCESS(200, "成功"),
    FAIL(400, "失败"),
    UNAUTHORIZED(401, "未认证"),
    NOT_FOUND(404, "接口不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // 业务错误码
    PARAM_LOST(1001, "参数缺失"),
    INDEX_LOST(1002, "指标不存在"),
    SQL_CONFIG_ERROR(1003, "SQL配置错误"),
    HAS_NOT_ACCESS(1004, "没有权限"),

    // 文件相关错误码
    FILE_EMPTY(2001, "文件为空"),
    FILE_TOO_LARGE(2002, "文件过大"),
    FILE_TYPE_NOT_SUPPORTED(2003, "文件类型不支持"),
    FILE_UPLOAD_FAILED(2004, "文件上传失败"),

    // 数据库相关错误码
    DATABASE_ERROR(3001, "数据库错误"),
    DATA_NOT_FOUND(3002, "数据不存在"),

    // HTTP 异常类型
    HTTP_MESSAGE_NOT_READABLE(4001, "请求体格式错误"),
    METHOD_ARGUMENT_NOT_VALID(4002, "参数验证失败"),
    CONSTRAINT_VIOLATION(4003, "约束违反"),
    MISSING_SERVLET_REQUEST_PARAMETER(4004, "缺少请求参数"),
    TYPE_MISMATCH(4005, "参数类型不匹配"),

    // 未知错误
    UNKNOWN_ERROR(9999, "未知错误");

    public static String path = "F:\\changan\\system\\"; // 返回路径dateMedio
    private final int code;
    private final String message;

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据异常类名获取对应的 Code 枚举
     */
    public static Code fromException(Class<?> exceptionClass) {
        if (exceptionClass == null) {
            return UNKNOWN_ERROR;
        }

        String simpleName = exceptionClass.getSimpleName();

        // 映射异常类名到 Code 枚举
        switch (simpleName) {
            case "HttpMessageNotReadableException":
                return HTTP_MESSAGE_NOT_READABLE;
            case "MethodArgumentNotValidException":
                return METHOD_ARGUMENT_NOT_VALID;
            case "ConstraintViolationException":
                return CONSTRAINT_VIOLATION;
            case "MissingServletRequestParameterException":
                return MISSING_SERVLET_REQUEST_PARAMETER;
            case "TypeMismatchException":
                return TYPE_MISMATCH;
            default:
                return UNKNOWN_ERROR;
        }
    }

    /**
     * 安全地根据名称获取枚举
     */
    public static Code safeValueOf(String name) {
        try {
            return Code.valueOf(name);
        } catch (IllegalArgumentException e) {
            return UNKNOWN_ERROR;
        }
    }

    /**
     * 根据 code 值获取枚举
     */
    public static Code valueOf(int code) {
        for (Code c : values()) {
            if (c.getCode() == code) {
                return c;
            }
        }
        return UNKNOWN_ERROR;
    }
}

//