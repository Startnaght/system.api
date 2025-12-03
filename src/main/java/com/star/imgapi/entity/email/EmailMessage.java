package com.star.imgapi.entity.email;

import com.star.imgapi.enums.EmailPriority;
import com.star.imgapi.enums.EmailStatus;
import com.star.imgapi.enums.EmailType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class EmailMessage {
    private String id;
    private String to;
    private String subject;
    private String content;
    private EmailType type;
    private EmailPriority priority;
    private Map<String, Object> variables;
    private LocalDateTime scheduledTime;
    private int retryCount = 0;
    private EmailStatus status = EmailStatus.PENDING;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    // 附件信息
    private Map<String, String> attachments;

    // 回调信息
    private String callbackUrl;
    private Map<String, Object> callbackData;
}

