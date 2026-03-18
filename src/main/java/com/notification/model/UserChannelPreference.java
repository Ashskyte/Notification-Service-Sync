package com.notification.model;

import com.notification.model.enums.NotificationChannel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_channel_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChannelPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
}
