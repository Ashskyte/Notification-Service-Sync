package com.notification.channel;

import com.notification.model.enums.NotificationChannel;
import com.notification.service.ChannelResolverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ChannelSelectionTest {

    private ChannelResolverService channelResolverService;

    @BeforeEach
    void setUp() {
        List<NotificationChannelHandler> handlers = List.of(
                new EmailChannelHandler(),
                new SmsChannelHandler(),
                new PushNotificationChannelHandler()
        );
        channelResolverService = new ChannelResolverService(handlers);
    }

    @Test
    @DisplayName("Should resolve EMAIL channel handler")
    void shouldResolveEmailChannel() {
        NotificationChannelHandler handler = channelResolverService.resolve(NotificationChannel.EMAIL);

        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(EmailChannelHandler.class);
        assertThat(handler.getChannel()).isEqualTo(NotificationChannel.EMAIL);
    }

    @Test
    @DisplayName("Should resolve SMS channel handler")
    void shouldResolveSmsChannel() {
        NotificationChannelHandler handler = channelResolverService.resolve(NotificationChannel.SMS);

        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(SmsChannelHandler.class);
        assertThat(handler.getChannel()).isEqualTo(NotificationChannel.SMS);
    }

    @Test
    @DisplayName("Should resolve PUSH_NOTIFICATION channel handler")
    void shouldResolvePushNotificationChannel() {
        NotificationChannelHandler handler = channelResolverService.resolve(NotificationChannel.PUSH_NOTIFICATION);

        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(PushNotificationChannelHandler.class);
        assertThat(handler.getChannel()).isEqualTo(NotificationChannel.PUSH_NOTIFICATION);
    }

    @Test
    @DisplayName("Should check if channel is supported")
    void shouldCheckChannelSupport() {
        assertThat(channelResolverService.isChannelSupported(NotificationChannel.EMAIL)).isTrue();
        assertThat(channelResolverService.isChannelSupported(NotificationChannel.SMS)).isTrue();
        assertThat(channelResolverService.isChannelSupported(NotificationChannel.PUSH_NOTIFICATION)).isTrue();
    }

    @Test
    @DisplayName("Each handler should support its own channel type")
    void eachHandlerShouldSupportOwnChannel() {
        EmailChannelHandler emailHandler = new EmailChannelHandler();
        SmsChannelHandler smsHandler = new SmsChannelHandler();
        PushNotificationChannelHandler pushHandler = new PushNotificationChannelHandler();

        assertThat(emailHandler.supports(NotificationChannel.EMAIL)).isTrue();
        assertThat(emailHandler.supports(NotificationChannel.SMS)).isFalse();

        assertThat(smsHandler.supports(NotificationChannel.SMS)).isTrue();
        assertThat(smsHandler.supports(NotificationChannel.EMAIL)).isFalse();

        assertThat(pushHandler.supports(NotificationChannel.PUSH_NOTIFICATION)).isTrue();
        assertThat(pushHandler.supports(NotificationChannel.EMAIL)).isFalse();
    }
}
