package com.star.imgapi.service.email.template;

/**
 * 自定义运行时异常，用于模板加载相关错误。
 */
public class TemplateLoadingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TemplateLoadingException(String message) {
        super(message);
    }

    public TemplateLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateLoadingException(Throwable cause) {
        super(cause);
    }
}