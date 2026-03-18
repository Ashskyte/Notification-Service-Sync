package com.notification.dto;

import com.notification.model.User;
import com.notification.model.enums.NotificationChannel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String deviceToken;
    private boolean active;
    private List<NotificationChannel> preferredChannels;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        List<NotificationChannel> channels = user.getChannelPreferences().stream()
                .filter(pref -> pref.isEnabled())
                .map(pref -> pref.getChannel())
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .deviceToken(user.getDeviceToken())
                .active(user.isActive())
                .preferredChannels(channels)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
