package com.star.imgapi.validation.annotation;


import com.star.imgapi.validation.validator.FileTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 文件类型验证注解
 * 用于验证上传文件的类型是否在允许的范围内
 */
@Documented
@Constraint(validatedBy = FileTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileType {

    /**
     * 错误消息
     */
    String message() default "不支持的文件类型";

    /**
     * 验证分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 允许的文件类型列表
     */
    String[] allowedTypes() default {
            "jpg", "jpeg", "png", "gif", "bmp", "webp",  // 图片
            "txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",  // 文档
            "zip", "rar", "7z",  // 压缩包
            "mp4", "avi", "mov", "mkv",  // 视频
            "mp3", "wav", "flac"  // 音频
    };

    /**
     * 最大文件大小（MB）
     */
    long maxSize() default 50;
}