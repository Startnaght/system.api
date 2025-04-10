package com.star.imgapi.exception;

/**
 * 异常信息枚举
 */
public enum ExceptionMessage {

    // 文件上传相关异常
    FILE_EMPTY("文件不能为空"),
    FILE_TOO_LARGE("文件大小超过限制"),
    FILE_TYPE_NOT_SUPPORTED("不支持的文件类型"),
    FILE_UPLOAD_FAILED("文件上传失败"),
    FILE_SAVE_FAILED("文件保存失败"),

    // 数据库相关异常
    DATABASE_CONNECTION_FAILED("数据库连接失败"),
    DATABASE_OPERATION_FAILED("数据库操作失败"),
    DATA_NOT_FOUND("数据不存在"),

    // API调用相关异常
    API_CALL_FAILED("API调用失败"),
    API_TIMEOUT("API调用超时"),
    API_RESPONSE_ERROR("API响应错误"),

    // 参数验证相关异常
    PARAM_REQUIRED("参数不能为空"),
    PARAM_INVALID("参数格式错误"),
    PARAM_TYPE_MISMATCH("参数类型不匹配"),

    // 系统相关异常
    SYSTEM_ERROR("系统内部错误"),
    SERVICE_UNAVAILABLE("服务暂时不可用");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}