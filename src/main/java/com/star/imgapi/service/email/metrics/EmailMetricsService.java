package com.star.imgapi.service.email.metrics;

import com.star.imgapi.entity.email.EmailMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EmailMetricsService {

    private final MeterRegistry meterRegistry;
    private final Map<String, AtomicLong> emailCounters = new ConcurrentHashMap<>();

    // 计数器
    private final Counter emailsSent;
    private final Counter emailsFailed;
    private final Counter emailsRetried;

    // 计时器
    private final Timer emailSendTimer;

    public EmailMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.emailsSent = Counter.builder("email.sent.total")
                .description("Total emails sent")
                .register(meterRegistry);

        this.emailsFailed = Counter.builder("email.failed.total")
                .description("Total emails failed")
                .register(meterRegistry);

        this.emailsRetried = Counter.builder("email.retried.total")
                .description("Total emails retried")
                .register(meterRegistry);

        this.emailSendTimer = Timer.builder("email.send.duration")
                .description("Email sending duration")
                .register(meterRegistry);
    }

    /**
     * 记录发送尝试
     */
    public void recordSendAttempt(EmailMessage email) {
        String typeKey = "email.type." + email.getType().name().toLowerCase();
        emailCounters.computeIfAbsent(typeKey, k ->
                meterRegistry.gauge(typeKey, new AtomicLong(0))).incrementAndGet();
    }

    /**
     * 记录发送成功
     */
    public void recordSendSuccess(EmailMessage email) {
        emailsSent.increment();

        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(emailSendTimer);

        // 记录类型特定的成功
        Counter.builder("email.sent")
                .tag("type", email.getType().name())
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录发送失败
     */
    public void recordSendFailure(EmailMessage email, Exception e) {
        emailsFailed.increment();

        Counter.builder("email.failed")
                .tag("type", email.getType().name())
                .tag("reason", e.getClass().getSimpleName())
                .register(meterRegistry)
                .increment();
    }

    /**
     * 获取邮件统计
//     */
//    public EmailStats getEmailStats() {
//        EmailStats stats = new EmailStats();
//        stats.setSentToday(getSentTodayCount());
//        stats.setFailedToday(getFailedTodayCount());
//        stats.setAverageSendTime(getAverageSendTime());
//
//        return stats;
//    }
}