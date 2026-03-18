package com.notification.dto;

import com.notification.model.enums.NotificationChannel;
import com.notification.model.enums.NotificationPriority;
import com.notification.model.enums.RecurrenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    private NotificationPriority priority;

    private LocalDateTime scheduledAt;

    private RecurrenceType recurrenceType;
}
