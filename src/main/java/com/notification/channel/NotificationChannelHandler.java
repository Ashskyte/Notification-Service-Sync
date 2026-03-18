package com.notification.channel;

import com.notification.model.Notification;
import com.notification.model.enums.NotificationChannel;

public interface NotificationChannelHandler {

    void send(Notification notification);

    NotificationChannel getChannel();

    boolean supports(NotificationChannel channel);
}
