package com.star.imgapi.validation.validator;
import com.star.imgapi.validation.annotation.ValidFileType;
import com.star.imgapi.util.FileTypeUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * 文件类型验证器实现
 */
public class FileTypeValidator implements ConstraintValidator<ValidFileType, MultipartFile> {

    private List<String> allowedTypes;
    private long maxSize;

    @Override
    public void initialize(ValidFileType constraintAnnotation) {
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowedTypes());
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // 如果文件为空，由 @NotNull 注解处理
        if (file == null || file.isEmpty()) {
            return true;
        }

        // 获取文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return buildErrorMessage(context, "文件名不能为空");
        }

        String fileExtension = FileTypeUtil.getFileExtension(originalFilename);
        if (fileExtension.isEmpty()) {
            return buildErrorMessage(context, "无法识别的文件类型");
        }

        // 验证文件类型
        boolean typeValid = false;
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(fileExtension)) {
                typeValid = true;
                break;
            }
        }

        if (!typeValid) {
            return buildErrorMessage(context,
                    String.format("不支持的文件类型: %s，支持的类型: %s",
                            fileExtension, String.join(", ", allowedTypes)));
        }

        // 验证文件大小
        if (file.getSize() > maxSize * 1024 * 1024) {
            return buildErrorMessage(context,
                    String.format("文件大小不能超过 %dMB", maxSize));
        }

        return true;
    }

    /**
     * 构建错误消息
     */
    private boolean buildErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        return false;
    }
}