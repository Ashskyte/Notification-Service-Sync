package com.notification.service;

import com.notification.channel.NotificationChannelHandler;
import com.notification.exception.NotificationException;
import com.notification.model.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChannelResolverService {

    private final Map<NotificationChannel, NotificationChannelHandler> handlerMap;

    public ChannelResolverService(List<NotificationChannelHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        NotificationChannelHandler::getChannel,
                        Function.identity()
                ));
        log.info("Registered notification channel handlers: {}", handlerMap.keySet());
    }

    public NotificationChannelHandler resolve(NotificationChannel channel) {
        NotificationChannelHandler handler = handlerMap.get(channel);
        if (handler == null) {
            throw new NotificationException("No handler registered for channel: " + channel);
        }
        return handler;
    }

    public boolean isChannelSupported(NotificationChannel channel) {
        return handlerMap.containsKey(channel);
    }
}
