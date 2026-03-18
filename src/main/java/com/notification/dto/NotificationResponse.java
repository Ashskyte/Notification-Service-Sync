package com.notification.dto;

import com.notification.model.Notification;
import com.notification.model.enums.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private NotificationChannel channel;
    private NotificationPriority priority;
    private NotificationStatus status;
    private RecurrenceType recurrenceType;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private int retryCount;
    private String failureReason;
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .channel(notification.getChannel())
                .priority(notification.getPriority())
                .status(notification.getStatus())
                .recurrenceType(notification.getRecurrenceType())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .retryCount(notification.getRetryCount())
                .failureReason(notification.getFailureReason())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
