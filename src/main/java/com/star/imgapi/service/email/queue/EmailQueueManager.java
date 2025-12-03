package com.star.imgapi.service.email.queue;

import com.star.imgapi.config.EmailConfigProperties;
import com.star.imgapi.entity.email.EmailMessage;
import com.star.imgapi.util.GlobalLog;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class EmailQueueManager {

    private final PriorityBlockingQueue<EmailTask> emailQueue;
    private final ThreadPoolExecutor emailExecutor;
    private final ScheduledExecutorService retryExecutor;

    @Autowired
    public EmailQueueManager(EmailConfigProperties config) {
        // 创建优先级队列，优先级数值小的优先处理
        this.emailQueue = new PriorityBlockingQueue<>(100,
                Comparator.comparingInt(task -> task.getEmail().getPriority().getValue()));

        // 创建邮件发送线程池
        this.emailExecutor = new ThreadPoolExecutor(
                config.getCorePoolSize(),
                config.getMaxPoolSize(),
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(config.getQueueCapacity()),
                new EmailThreadFactory(),
                new EmailRejectionHandler()
        );

        // 创建重试调度器
        this.retryExecutor = Executors.newScheduledThreadPool(2, new RetryThreadFactory());

        // 启动队列处理器
        startQueueProcessor();

        GlobalLog.info("邮件队列管理器初始化完成，核心线程数: " + config.getCorePoolSize());
    }

    /**
     * 将邮件加入队列
     */
    public void enqueueEmail(EmailMessage email) {
        try {
            EmailTask task = new EmailTask(email, this);
            emailQueue.put(task);
            GlobalLog.debug("邮件已加入队列: " + email.getId() + ", 优先级: " + email.getPriority());
        } catch (Exception e) {
            GlobalLog.error("邮件入队失败: " + email.getId() + ", 错误: " + e.getMessage());
            throw new EmailQueueException("邮件入队失败", e);
        }
    }

    /**
     * 安排重试
     */
    public void scheduleRetry(EmailMessage email, long delay) {
        retryExecutor.schedule(() -> {
            try {
                email.setRetryCount(email.getRetryCount() + 1);
                enqueueEmail(email);
                GlobalLog.info("安排邮件重试: " + email.getId() + ", 重试次数: " + email.getRetryCount());
            } catch (Exception e) {
                GlobalLog.error("安排重试失败: " + email.getId() + ", 错误: " + e.getMessage());
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 启动队列处理器
     */
    private void startQueueProcessor() {
        // 启动多个消费者处理队列中的邮件
        int processorCount = 3; // 可以根据配置调整
        for (int i = 0; i < processorCount; i++) {
            emailExecutor.submit(this::processEmailQueue);
        }
    }

    /**
     * 处理邮件队列
     */
    private void processEmailQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                EmailTask task = emailQueue.take(); // 阻塞直到有任务
                processEmailTask(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GlobalLog.warn("邮件队列处理器被中断");
                break;
            } catch (Exception e) {
                GlobalLog.error("处理邮件队列异常: " + e.getMessage());
            }
        }
    }

    /**
     * 处理单个邮件任务
     */
    private void processEmailTask(EmailTask task) {
        try {
            GlobalLog.debug("开始处理邮件任务: " + task.getEmail().getId());
            task.execute();
        } catch (Exception e) {
            GlobalLog.error("处理邮件任务失败: " + task.getEmail().getId() + ", 错误: " + e.getMessage());
        }
    }

    /**
     * 获取队列状态
     */
    public QueueStatus getQueueStatus() {
        QueueStatus status = new QueueStatus();
        status.setQueueSize(emailQueue.size());
        status.setActiveCount(emailExecutor.getActiveCount());
        status.setQueueCapacity(emailExecutor.getQueue().remainingCapacity());
        status.setCompletedTaskCount(emailExecutor.getCompletedTaskCount());
        status.setPoolSize(emailExecutor.getPoolSize());

        return status;
    }

    /**
     * 优雅关闭
     */
    public void shutdown() {
        GlobalLog.info("开始关闭邮件队列管理器...");

        // 停止接受新任务
        emailExecutor.shutdown();
        retryExecutor.shutdown();

        try {
            // 等待现有任务完成
            if (!emailExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                emailExecutor.shutdownNow();
            }

            if (!retryExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                retryExecutor.shutdownNow();
            }

            GlobalLog.info("邮件队列管理器已关闭");
        } catch (InterruptedException e) {
            emailExecutor.shutdownNow();
            retryExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ==================== 内部类 ====================

    /**
     * 邮件任务类
     */
    public static class EmailTask {
        @Getter
        private final EmailMessage email;
        private final EmailQueueManager queueManager;

        public EmailTask(EmailMessage email, EmailQueueManager queueManager) {
            this.email = email;
            this.queueManager = queueManager;
        }

        /// 这是个内部类
        public void execute() {
            /// 这里调用实际的邮件发送逻辑
            // 暂时用模拟实现
            simulateEmailSending();
        }

        private void simulateEmailSending() {
            try {
                GlobalLog.info("模拟发送邮件: " + email.getId() + " 到 " + email.getTo());
                Thread.sleep(100); // 模拟网络延迟

                // 模拟90%成功率
                if (Math.random() > 0.1) {
                    GlobalLog.info("邮件发送成功: " + email.getId());
                } else {
                    throw new RuntimeException("模拟发送失败");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("邮件发送被中断", e);
            } catch (Exception e) {
                throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 邮件线程工厂
     */
    private static class EmailThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "email-worker-" + counter.getAndIncrement());
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }

    /**
     * 重试线程工厂
     */
    private static class RetryThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "email-retry-" + counter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }

    /**
     * 拒绝策略处理器
     */
    private static class EmailRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            GlobalLog.warn("邮件任务被拒绝，队列已满。活跃线程: " +
                    executor.getActiveCount() + ", 队列大小: " + executor.getQueue().size());

            // 可以在这里记录指标或发送警报
            if (r instanceof EmailTask) {
                EmailTask task = (EmailTask) r;
                GlobalLog.error("邮件任务被拒绝: " + task.getEmail().getId());
            }
        }
    }

    /**
     * 队列状态类
     */
    public static class QueueStatus {
        private int queueSize;
        private int activeCount;
        private int queueCapacity;
        private long completedTaskCount;
        private int poolSize;

        // Getter 和 Setter
        public int getQueueSize() { return queueSize; }
        public void setQueueSize(int queueSize) { this.queueSize = queueSize; }

        public int getActiveCount() { return activeCount; }
        public void setActiveCount(int activeCount) { this.activeCount = activeCount; }

        public int getQueueCapacity() { return queueCapacity; }
        public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }

        public long getCompletedTaskCount() { return completedTaskCount; }
        public void setCompletedTaskCount(long completedTaskCount) { this.completedTaskCount = completedTaskCount; }

        public int getPoolSize() { return poolSize; }
        public void setPoolSize(int poolSize) { this.poolSize = poolSize; }

        @Override
        public String toString() {
            return String.format("QueueStatus{队列大小=%d, 活跃线程=%d, 队列容量=%d, 完成数=%d, 池大小=%d}",
                    queueSize, activeCount, queueCapacity, completedTaskCount, poolSize);
        }
    }
}