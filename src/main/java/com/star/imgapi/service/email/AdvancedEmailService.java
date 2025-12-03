package com.star.imgapi.service.email;

import com.star.imgapi.config.EmailConfigProperties;
import com.star.imgapi.entity.email.*;
import com.star.imgapi.enums.EmailPriority;
import com.star.imgapi.enums.EmailStatus;
import com.star.imgapi.enums.EmailType;
import com.star.imgapi.util.DatabaseUtil;
import com.star.imgapi.util.GlobalLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AdvancedEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private EmailConfigProperties emailConfig;

    @Autowired
    private DatabaseUtil databaseUtil;

    // 邮箱验证正则表达式
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * 发送模板邮件 - 修复模板加载逻辑
     */
    public CompletableFuture<EmailResult> sendTemplatedEmail(EmailMessage email) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 验证邮件参数
                validateEmail(email);

                // 构建邮件内容
                MimeMessage message = buildTemplatedMimeMessage(email);

                // 发送邮件
                mailSender.send(message);

                // 记录发送成功
                EmailResult result = EmailResult.success(email.getId());
          //      databaseUtil.insertEmailRecord(email, "SENT", null);

                GlobalLog.info("模板邮件发送成功: " + email.getTo() + ", 模板: " +
                        (email.getType() != null ? email.getType().name() : "默认"));

                return result;

            } catch (Exception e) {
                GlobalLog.error("模板邮件发送失败: " + e.getMessage());

                // 记录发送失败
              //  databaseUtil.insertEmailRecord(email, "FAILED", e.getMessage());
                return EmailResult.failure(email.getId(), e.getMessage());
            }
        });
    }

    /**
     * 构建模板邮件内容
     */
    private MimeMessage buildTemplatedMimeMessage(EmailMessage email) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 设置基本属性
        setupBasicEmailProperties(helper, email);

        // 渲染模板
        String htmlContent = renderEmailTemplate(email);
        helper.setText(htmlContent, true);

        return message;
    }

    /**
     * 渲染邮件模板 - 修复模板加载路径
     */
    private String renderEmailTemplate(EmailMessage email) throws IOException {
        // 确定模板名称
        String templateName = getTemplateName(email);

        // 创建Thymeleaf上下文
        Context context = new Context();
        if (email.getVariables() != null) {
            context.setVariables(email.getVariables());
        }

        try {
            // 使用Thymeleaf渲染模板
            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            GlobalLog.warn("模板渲染失败: " + templateName + ", 使用默认模板. 错误: " + e.getMessage());

            // 如果指定模板渲染失败，使用默认模板
            return renderDefaultTemplate(email);
        }
    }

    /**
     * 获取模板名称
     */
    private String getTemplateName(EmailMessage email) {
        if (email.getType() != null) {
            // 根据邮件类型确定模板名称
            switch (email.getType()) {
                case SYSTEM_ALERT:
                    return "system-alert";
                //case FILE_UPLOAD:
                 //   return "file-upload";
                case USER_REGISTRATION:
                    return "user-registration";
                //case PASSWORD_RESET:
                  //  return "password-reset";
                // case HITOKOTO_SHARE:
                  //  return "hitokoto-share";
                case BILLING_NOTIFICATION:
                    return "billing";
                case SECURITY_ALERT:
                    return "security-alert";
                case API_USAGE_REPORT:
                    return "api-report";
                default:
                    return "default";
            }
        }
        return "default";
    }

    /**
     * 渲染默认模板（备用方案）
     */
    private String renderDefaultTemplate(EmailMessage email) {
        // 简单的默认HTML模板
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<title>").append(escapeHtml(email.getSubject())).append("</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: #f9f9f9; padding: 20px; border-radius: 5px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<h2>").append(escapeHtml(email.getSubject())).append("</h2>");

        // 添加变量内容
        if (email.getVariables() != null && !email.getVariables().isEmpty()) {
            for (Map.Entry<String, Object> entry : email.getVariables().entrySet()) {
                html.append("<p><strong>").append(escapeHtml(entry.getKey())).append(":</strong> ")
                        .append(escapeHtml(String.valueOf(entry.getValue()))).append("</p>");
            }
        }

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * 检查模板文件是否存在
     */
    private boolean templateExists(String templateName) {
        try {
            String templatePath = "templates/email/" + templateName + ".html";
            ClassPathResource resource = new ClassPathResource(templatePath);
            return resource.exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 手动加载模板内容（备用方法）
     */
    private String loadTemplateManually(String templateName) throws IOException {
        String templatePath = "templates/email/" + templateName + ".html";
        ClassPathResource resource = new ClassPathResource(templatePath);

        if (!resource.exists()) {
            throw new IOException("模板文件不存在: " + templatePath);
        }

        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * HTML转义
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // ==================== 原有方法保持不动，只修复模板相关部分 ====================

    /**
     * 发送简单邮件
     */
    public CompletableFuture<EmailResult> sendSimpleEmail(String to, String subject, String text) {
        EmailMessage email = new EmailMessage();
        email.setId(generateEmailId());
        email.setTo(to);
        email.setSubject(subject);
        email.setContent(text);
        email.setType(EmailType.SYSTEM_ALERT);

        return sendEmail(email);
    }

    /**
     * 发送邮件通用方法
     */
    public CompletableFuture<EmailResult> sendEmail(EmailMessage email) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 验证邮件参数
                validateEmail(email);

                // 构建邮件内容
                MimeMessage message;
                if (email.getContent() != null && email.getContent().contains("<html>")) {
                    // HTML内容
                    message = buildHtmlMimeMessage(email);
                } else {
                    // 纯文本内容
                    message = buildTextMimeMessage(email);
                }

                // 发送邮件
                mailSender.send(message);

                // 记录发送成功
                EmailResult result = EmailResult.success(email.getId());
               // databaseUtil.insertEmailRecord(email, "SENT", null);

                GlobalLog.info("邮件发送成功: " + email.getTo());
                return result;

            } catch (Exception e) {
                GlobalLog.error("邮件发送失败: " + e.getMessage());

                // 记录发送失败
               //  databaseUtil.insertEmailRecord(email, "FAILED", e.getMessage());
                return EmailResult.failure(email.getId(), e.getMessage());
            }
        });
    }

    /**
     * 构建HTML邮件
     */
    private MimeMessage buildHtmlMimeMessage(EmailMessage email) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        setupBasicEmailProperties(helper, email);
        helper.setText(email.getContent(), true);

        return message;
    }

    /**
     * 构建纯文本邮件
     */
    private MimeMessage buildTextMimeMessage(EmailMessage email) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        setupBasicEmailProperties(helper, email);
        helper.setText(email.getContent(), false);

        return message;
    }

    /**
     * 设置邮件基本属性
     */
    private void setupBasicEmailProperties(MimeMessageHelper helper, EmailMessage email)
            throws MessagingException, UnsupportedEncodingException {
        helper.setFrom(emailConfig.getFromAddress(), emailConfig.getFromName());
        helper.setTo(email.getTo().split(";"));
        helper.setSubject(email.getSubject());
        helper.setSentDate(new Date());

        // 设置优先级
        if (email.getPriority() == EmailPriority.HIGHEST) {
            helper.setPriority(1);
        }
    }

    /**
     * 验证邮件参数
     */
    private void validateEmail(EmailMessage email) {
        if (email == null) {
            throw new IllegalArgumentException("邮件对象不能为空");
        }

        if (email.getTo() == null || email.getTo().trim().isEmpty()) {
            throw new IllegalArgumentException("收件人地址不能为空");
        }

        if (!isValidEmail(email.getTo())) {
            throw new IllegalArgumentException("无效的邮箱地址: " + email.getTo());
        }

        if (email.getSubject() == null || email.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("邮件主题不能为空");
        }
    }

    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 生成邮件ID
     */
    private String generateEmailId() {
        return "email-" + System.currentTimeMillis() + "-" +
                java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public BatchEmailResult sendBatchEmails(List<EmailMessage> emails) {
        List<EmailResult> results = emails.stream()
                .map(email -> sendEmail(email).join())
                .collect(Collectors.toList());

        BatchEmailResult batchResult = new BatchEmailResult("error");
        batchResult.setTotal(results.size());
        batchResult.setResults(results);
        return batchResult;
    }


/// 暂时没有使用修改动作
    public EmailStatus getEmailStatus(String emailId) {
        return EmailStatus.SENT;
    }

    public boolean cancelEmail(String emailId) {
        return true;
    }

    public EmailResult retryEmail(String emailId) {
        return null;
    }
}