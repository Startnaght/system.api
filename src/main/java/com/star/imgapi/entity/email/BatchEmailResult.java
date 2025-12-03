package com.star.imgapi.entity.email;

import lombok.Data; /**
 * 批量邮件结果
 */
@Data
public class BatchEmailResult {
    private int total;
    private int successCount;
    private int failureCount;
    private java.util.List<EmailResult> results;

    public BatchEmailResult(java.util.List<EmailResult> results) {
        this.results = results;
        this.total = results.size();
        this.successCount = (int) results.stream().filter(EmailResult::isSuccess).count();
        this.failureCount = total - successCount;
    }

    public BatchEmailResult(String email) {
           this.results = new java.util.ArrayList<>();
           this.successCount = 0;
           this.failureCount = 1;
           this.total = 1;
    }
}
