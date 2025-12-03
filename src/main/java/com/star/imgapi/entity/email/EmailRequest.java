package com.star.imgapi.entity.email;

import com.star.imgapi.enums.EmailPriority;
import com.star.imgapi.enums.EmailType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 邮件发送请求DTO
 */
@Data
public class EmailRequest {
    private String to;
    private String subject;
    private EmailType type;
    private EmailPriority priority = EmailPriority.NORMAL;
    private Map<String, Object> variables;
    private LocalDateTime scheduledTime;
}

