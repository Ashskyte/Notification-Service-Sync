package com.notification.service;

import com.notification.channel.NotificationChannelHandler;
import com.notification.model.Notification;
import com.notification.model.enums.NotificationStatus;
import com.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

    private final NotificationRepository notificationRepository;
    private final ChannelResolverService channelResolverService;

    @Value("${notification.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${notification.retry.initial-delay-ms:1000}")
    private long initialDelayMs;

    @Value("${notification.retry.multiplier:2.0}")
    private double multiplier;

    @Transactional
    public void retryFailedNotifications() {
        List<Notification> retryable = notificationRepository.findRetryableNotifications(NotificationStatus.FAILED);

        if (retryable.isEmpty()) {
            return;
        }

        log.info("Found {} notifications eligible for retry", retryable.size());

        for (Notification notification : retryable) {
            retryNotification(notification);
        }
    }

    public void retryNotification(Notification notification) {
        if (notification.getRetryCount() >= notification.getMaxRetries()) {
            log.warn("Notification [{}] exceeded max retries ({}). Marking as permanently FAILED.",
                    notification.getId(), notification.getMaxRetries());
            return;
        }

        long delay = calculateExponentialBackoff(notification.getRetryCount());
        log.info("Retrying notification [{}], attempt {}/{}, delay={}ms",
                notification.getId(),
                notification.getRetryCount() + 1,
                notification.getMaxRetries(),
                delay);

        try {
            Thread.sleep(delay);

            NotificationChannelHandler handler = channelResolverService.resolve(notification.getChannel());
            handler.send(notification);

            notification.setStatus(NotificationStatus.SENT);
            notification.setRetryCount(notification.getRetryCount() + 1);
            log.info("Notification [{}] sent successfully on retry attempt {}",
                    notification.getId(), notification.getRetryCount());

        } catch (Exception e) {
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setFailureReason(e.getMessage());

            if (notification.getRetryCount() >= notification.getMaxRetries()) {
                notification.setStatus(NotificationStatus.FAILED);
                log.error("Notification [{}] permanently failed after {} retries: {}",
                        notification.getId(), notification.getRetryCount(), e.getMessage());
            } else {
                notification.setStatus(NotificationStatus.RETRY);
                log.warn("Notification [{}] retry attempt {} failed: {}",
                        notification.getId(), notification.getRetryCount(), e.getMessage());
            }
        }

        notificationRepository.save(notification);
    }

    public long calculateExponentialBackoff(int retryCount) {
        return (long) (initialDelayMs * Math.pow(multiplier, retryCount));
    }
}
