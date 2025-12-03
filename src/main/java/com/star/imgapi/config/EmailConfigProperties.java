package com.star.imgapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
@Data
@Component
@ConfigurationProperties(prefix = "app.email")
public class EmailConfigProperties {

    // 基础配置
    private String fromAddress;
    private String fromName;
    private boolean enabled = true;
    private int maxRetryAttempts = 3;
    private long retryInterval = 5000; // 5秒

    // 模板配置
    private String templateLocation = "classpath:/templates/email/";
    private boolean templateCache = true;

    // 异步配置
    private boolean asyncEnabled = true;
    private int corePoolSize = 5;
    private int maxPoolSize = 20;
    private int queueCapacity = 100;

    // 重试策略
    private Map<String, Integer> retryPolicy = Map.of(
            "SEND_FAILED", 3,
            "NETWORK_ERROR", 5,
            "AUTH_FAILED", 1
    );

    // 邮件类型配置
    private Map<String, EmailTypeConfig> types;

    // Getter/Setter...

    public static class EmailTypeConfig {
        private String template;
        private String subject;
        private int priority;
        private boolean async;
        private int timeout;
        private boolean requireReceipt;

        // Getter/Setter...
    }
}