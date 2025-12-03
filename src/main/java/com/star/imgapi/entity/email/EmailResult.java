package com.star.imgapi.entity.email;

import lombok.Data;

import java.time.LocalDateTime; /**
 * 邮件发送结果
 */
@Data
public class EmailResult {
    private String emailId;
    private boolean success;
    private String message;
    private LocalDateTime sentTime;

    public static EmailResult success(String emailId) {
        EmailResult result = new EmailResult();
        result.setEmailId(emailId);
        result.setSuccess(true);
        result.setMessage("发送成功");
        result.setSentTime(LocalDateTime.now());
        return result;
    }

    public static EmailResult failure(String emailId, String errorMessage) {
        EmailResult result = new EmailResult();
        result.setEmailId(emailId);
        result.setSuccess(false);
        result.setMessage(errorMessage);
        result.setSentTime(LocalDateTime.now());
        return result;
    }
}
