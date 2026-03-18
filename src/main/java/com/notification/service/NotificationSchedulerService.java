package com.notification.service;

import com.notification.model.Notification;
import com.notification.model.enums.NotificationStatus;

import com.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final RetryService retryService;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    @Transactional
    public void processScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> scheduledNotifications = notificationRepository
                .findScheduledNotificationsReady(NotificationStatus.SCHEDULED, now);

        if (!scheduledNotifications.isEmpty()) {
            log.info("Processing {} scheduled notifications", scheduledNotifications.size());
            for (Notification notification : scheduledNotifications) {
                notificationService.processNotification(notification);
            }
        }
    }

    @Scheduled(fixedRate = 60000) // Every 60 seconds
    @Transactional
    public void processRecurringNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> recurringNotifications = notificationRepository
                .findRecurringNotificationsDue(now);

        if (!recurringNotifications.isEmpty()) {
            log.info("Processing {} recurring notifications", recurringNotifications.size());
            for (Notification original : recurringNotifications) {
                Notification recurring = Notification.builder()
                        .user(original.getUser())
                        .title(original.getTitle())
                        .message(original.getMessage())
                        .channel(original.getChannel())
                        .priority(original.getPriority())
                        .recurrenceType(original.getRecurrenceType())
                        .status(NotificationStatus.PENDING)
                        .build();

                recurring = notificationRepository.save(recurring);
                notificationService.processNotification(recurring);

                original.setNextRecurrenceAt(
                        notificationService.calculateNextRecurrence(now, original.getRecurrenceType()));
                notificationRepository.save(original);
            }
        }
    }

    @Scheduled(fixedRate = 120000) // Every 2 minutes
    public void retryFailedNotifications() {
        log.debug("Running retry job for failed notifications");
        retryService.retryFailedNotifications();
    }
}
