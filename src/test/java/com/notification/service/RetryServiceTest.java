package com.notification.service;

import com.notification.channel.NotificationChannelHandler;
import com.notification.model.Notification;
import com.notification.model.User;
import com.notification.model.enums.*;
import com.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ChannelResolverService channelResolverService;

    @Mock
    private NotificationChannelHandler emailHandler;

    @InjectMocks
    private RetryService retryService;

    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(retryService, "maxRetryAttempts", 3);
        ReflectionTestUtils.setField(retryService, "initialDelayMs", 100);
        ReflectionTestUtils.setField(retryService, "multiplier", 2.0);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    @Test
    @DisplayName("Should calculate exponential backoff correctly")
    void shouldCalculateExponentialBackoff() {
        assertThat(retryService.calculateExponentialBackoff(0)).isEqualTo(100);  // 100 * 2^0
        assertThat(retryService.calculateExponentialBackoff(1)).isEqualTo(200);  // 100 * 2^1
        assertThat(retryService.calculateExponentialBackoff(2)).isEqualTo(400);  // 100 * 2^2
    }

    @Test
    @DisplayName("Should retry and succeed on second attempt")
    void shouldRetryAndSucceed() {
        Notification notification = Notification.builder()
                .id(1L)
                .user(testUser)
                .title("Test")
                .message("Test message")
                .channel(NotificationChannel.EMAIL)
                .priority(NotificationPriority.HIGH)
                .status(NotificationStatus.FAILED)
                .recurrenceType(RecurrenceType.NONE)
                .retryCount(0)
                .maxRetries(3)
                .build();

        when(channelResolverService.resolve(NotificationChannel.EMAIL)).thenReturn(emailHandler);
        doNothing().when(emailHandler).send(any(Notification.class));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        retryService.retryNotification(notification);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.getRetryCount()).isEqualTo(1);
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should mark as FAILED after max retries exceeded")
    void shouldMarkAsFailedAfterMaxRetries() {
        Notification notification = Notification.builder()
                .id(1L)
                .user(testUser)
                .title("Test")
                .message("Test message")
                .channel(NotificationChannel.EMAIL)
                .priority(NotificationPriority.HIGH)
                .status(NotificationStatus.FAILED)
                .recurrenceType(RecurrenceType.NONE)
                .retryCount(2)
                .maxRetries(3)
                .build();

        when(channelResolverService.resolve(NotificationChannel.EMAIL)).thenReturn(emailHandler);
        doThrow(new RuntimeException("Send failed")).when(emailHandler).send(any(Notification.class));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        retryService.retryNotification(notification);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(notification.getRetryCount()).isEqualTo(3);
        assertThat(notification.getFailureReason()).isEqualTo("Send failed");
    }

    @Test
    @DisplayName("Should not retry when max retries already reached")
    void shouldNotRetryWhenMaxRetriesReached() {
        Notification notification = Notification.builder()
                .id(1L)
                .user(testUser)
                .title("Test")
                .message("Test message")
                .channel(NotificationChannel.EMAIL)
                .priority(NotificationPriority.HIGH)
                .status(NotificationStatus.FAILED)
                .recurrenceType(RecurrenceType.NONE)
                .retryCount(3)
                .maxRetries(3)
                .build();

        retryService.retryNotification(notification);

        verify(channelResolverService, never()).resolve(any());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should set status to RETRY on intermediate failure")
    void shouldSetRetryStatusOnIntermediateFailure() {
        Notification notification = Notification.builder()
                .id(1L)
                .user(testUser)
                .title("Test")
                .message("Test message")
                .channel(NotificationChannel.EMAIL)
                .priority(NotificationPriority.HIGH)
                .status(NotificationStatus.FAILED)
                .recurrenceType(RecurrenceType.NONE)
                .retryCount(0)
                .maxRetries(3)
                .build();

        when(channelResolverService.resolve(NotificationChannel.EMAIL)).thenReturn(emailHandler);
        doThrow(new RuntimeException("Temporary failure")).when(emailHandler).send(any(Notification.class));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        retryService.retryNotification(notification);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.RETRY);
        assertThat(notification.getRetryCount()).isEqualTo(1);
    }
}
