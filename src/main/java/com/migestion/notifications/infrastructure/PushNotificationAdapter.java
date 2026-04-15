package com.migestion.notifications.infrastructure;

import com.migestion.notifications.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationAdapter implements ChannelNotificationAdapter {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationAdapter.class);

    @Override
    public boolean supports(String canal) {
        return "PUSH".equalsIgnoreCase(canal);
    }

    @Override
    public void send(NotificationRequest request) {
        log.info("[Push simulation] UserId: {} | UserType: {} | Title: {} | Message: {}",
            request.usuarioId(),
            request.usuarioTipo(),
                request.titulo(),
                request.mensaje());
    }
}