package com.star.imgapi.dto.request;

import com.star.imgapi.validation.annotation.ValidHitokotoCategory;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class HitokotoRequest {

    @NotBlank(message = "分类不能为空")
    @ValidHitokotoCategory(allowedCategories = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k","l"})
    private String category = "k";

    private String encode;
    private String charset;
    private Integer minLength;
    private Integer maxLength;
}