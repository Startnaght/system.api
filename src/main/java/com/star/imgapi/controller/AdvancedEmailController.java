package com.star.imgapi.controller;

import com.star.imgapi.entity.email.*;
import com.star.imgapi.enums.EmailStatus;
import com.star.imgapi.service.email.AdvancedEmailService;
import com.star.imgapi.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/email")
public class AdvancedEmailController {

    @Autowired
    private AdvancedEmailService emailService;

    /**
     * 发送模板邮件
     */
    @PostMapping("/send")
    public CompletableFuture<ResponseWrapper<EmailResult>> sendEmail(
            @RequestBody EmailRequest request) {

        EmailMessage email = convertToEmailMessage(request);

        return emailService.sendEmail(email)
                .thenApply(ResponseWrapper::success)
                .exceptionally(e -> ResponseWrapper.error("邮件发送失败: " + e.getMessage()));
    }

    /**
     * 批量发送邮件
     */
    @PostMapping("/send-batch")
    public ResponseWrapper<BatchEmailResult> sendBatchEmails(
            @RequestBody BatchEmailRequest request) {

        List<EmailMessage> emails = request.getEmails().stream()
                .map(this::convertToEmailMessage)
                .collect(Collectors.toList());

        BatchEmailResult result = emailService.sendBatchEmails(emails);
        return ResponseWrapper.success(result);
    }

    /**
     * 获取邮件发送状态
     */
    @GetMapping("/status/{emailId}")
    public ResponseWrapper<EmailStatus> getEmailStatus(@PathVariable String emailId) {
        EmailStatus status = emailService.getEmailStatus(emailId);
        return ResponseWrapper.success(status);
    }

    /**
     * 取消待发送邮件
     */
    @PostMapping("/cancel/{emailId}")
    public ResponseWrapper<Boolean> cancelEmail(@PathVariable String emailId) {
        boolean cancelled = emailService.cancelEmail(emailId);
        return ResponseWrapper.success(cancelled);
    }

    /**
     * 重新发送失败邮件
     */
    @PostMapping("/retry/{emailId}")
    public ResponseWrapper<EmailResult> retryEmail(@PathVariable String emailId) {
        EmailResult result = emailService.retryEmail(emailId);
        return ResponseWrapper.success(result);
    }

    private EmailMessage convertToEmailMessage(EmailRequest request) {
        EmailMessage email = new EmailMessage();
        email.setId(generateEmailId());
        email.setTo(request.getTo());
        email.setSubject(request.getSubject());
        email.setType(request.getType());
        email.setPriority(request.getPriority());
        email.setVariables(request.getVariables());
        email.setScheduledTime(request.getScheduledTime());
     ///   email.setCallbackUrl(request.getCallbackUrl());
     ///  email.setCallbackData(request.getCallbackData());

        return email;
    }

    private String generateEmailId() {
        return "email-" + System.currentTimeMillis() + "-" +
                java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}