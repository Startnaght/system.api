package com.star.imgapi.validation.validator;

import com.star.imgapi.validation.annotation.ValidHitokotoCategory;
import com.star.imgapi.util.GlobalLog;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 一言分类验证器实现
 */
public class HitokotoCategoryValidator implements ConstraintValidator<ValidHitokotoCategory, String> {

    private Set<String> allowedCategories;
    private boolean ignoreCase;
    private boolean allowEmpty;
    private String[] originalAllowedCategories;

    @Override
    public void initialize(ValidHitokotoCategory constraintAnnotation) {
        this.originalAllowedCategories = constraintAnnotation.allowedCategories();
        this.ignoreCase = constraintAnnotation.ignoreCase();
        this.allowEmpty = constraintAnnotation.allowEmpty();

        // 初始化允许的分类集合
        this.allowedCategories = new HashSet<>();
        for (String category : originalAllowedCategories) {
            if (ignoreCase) {
                allowedCategories.add(category.toLowerCase());
            } else {
                allowedCategories.add(category);
            }
        }

        GlobalLog.debug("一言分类验证器初始化完成，允许的分类: " + Arrays.toString(originalAllowedCategories));
    }

    @Override
    public boolean isValid(String category, ConstraintValidatorContext context) {
        // 处理空值
        if (!StringUtils.hasText(category)) {
            if (allowEmpty) {
                return true;
            } else {
                return buildErrorMessage(context, "分类不能为空");
            }
        }

        // 处理分类验证
        String categoryToCheck = ignoreCase ? category.toLowerCase() : category;
        boolean isValid = allowedCategories.contains(categoryToCheck);

        if (!isValid) {
            return buildErrorMessage(context,
                    String.format("无效的分类: %s，允许的分类: %s",
                            category, Arrays.toString(originalAllowedCategories)));
        }

        return true;
    }

    /**
     * 构建自定义错误消息
     */
    private boolean buildErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        return false;
    }

    /**
     * 获取分类描述信息（用于日志和错误消息）
     */
    public static String getCategoryDescription(String category) {
        if (category == null) return "未知分类";

        switch (category.toLowerCase()) {
            case "a": return "动画";
            case "b": return "漫画";
            case "c": return "游戏";
            case "d": return "文学";
            case "e": return "原创";
            case "f": return "来自网络";
            case "g": return "其他";
            case "h": return "影视";
            case "i": return "诗词";
            case "j": return "网易云";
            case "k": return "哲学";
            default: return "未知分类(" + category + ")";
        }
    }

    /**
     * 获取所有分类的描述映射
     */
    public static java.util.Map<String, String> getAllCategoryDescriptions() {
        java.util.Map<String, String> descriptions = new java.util.HashMap<>();
        descriptions.put("a", "动画");
        descriptions.put("b", "漫画");
        descriptions.put("c", "游戏");
        descriptions.put("d", "文学");
        descriptions.put("e", "原创");
        descriptions.put("f", "来自网络");
        descriptions.put("g", "其他");
        descriptions.put("h", "影视");
        descriptions.put("i", "诗词");
        descriptions.put("j", "网易云");
        descriptions.put("k", "哲学");
        return descriptions;
    }
}