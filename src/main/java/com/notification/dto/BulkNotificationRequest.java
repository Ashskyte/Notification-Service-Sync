package com.notification.dto;

import com.notification.model.enums.NotificationChannel;
import com.notification.model.enums.NotificationPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkNotificationRequest {

    @NotEmpty(message = "User IDs list cannot be empty")
    private List<Long> userIds;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    private NotificationPriority priority;
}
