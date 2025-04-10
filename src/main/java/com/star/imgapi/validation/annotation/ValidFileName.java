package com.star.imgapi.validation.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

/**
 * 文件名验证注解
 */
@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "^[a-zA-Z0-9_\\-\\\\.\\s]{1,255}$",
        message = "文件名只能包含字母、数字、下划线、连字符、点和空格，且长度不超过255个字符")
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileName {
    String message() default "无效的文件名";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}