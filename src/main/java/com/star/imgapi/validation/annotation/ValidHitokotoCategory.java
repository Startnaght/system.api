package com.star.imgapi.validation.annotation;


import com.star.imgapi.validation.validator.HitokotoCategoryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 一言分类验证注解
 * 用于验证一言API的分类参数是否有效
 */
@Documented
@Constraint(validatedBy = HitokotoCategoryValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHitokotoCategory {

    /**
     * 默认错误消息
     */
    String message() default "无效的一言分类";

    /**
     * 验证分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 允许的分类列表
     * 默认支持一言API的所有标准分类
     */
    String[] allowedCategories() default {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"
    };

    /**
     * 是否忽略大小写
     */
    boolean ignoreCase() default true;

    /**
     * 是否允许为空
     */
    boolean allowEmpty() default false;
}