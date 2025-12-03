package com.star.imgapi.entity.email;

import lombok.Data; /**
 * 批量邮件请求DTO
 */
@Data
public class BatchEmailRequest {
    private java.util.List<EmailRequest> emails;
}
