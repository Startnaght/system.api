package com.star.imgapi.enums;

import lombok.Data;
import lombok.Getter;

// 邮件类型枚举

public enum EmailType {
    SYSTEM_ALERT("系统警报", "system-alert", true),
    FILE_UPLOAD_NOTIFICATION("文件上传通知", "file-upload", false),
    USER_REGISTRATION("用户注册", "user-registration", false),
    PASSWORD_RESET("密码重置", "password-reset", true),
    BILLING_NOTIFICATION("账单通知", "billing", true),
    SECURITY_ALERT("安全警报", "security-alert", true),
    API_USAGE_REPORT("API使用报告", "api-report", false);

    private final String description;
    @Getter
    private final String templateName;
    private final boolean highPriority;

    EmailType(String description, String templateName, boolean highPriority) {
        this.description = description;
        this.templateName = templateName;
        this.highPriority = highPriority;
    }


}
