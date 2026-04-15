package com.migestion.notifications.infrastructure;

import com.migestion.notifications.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppNotificationAdapter implements ChannelNotificationAdapter {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationAdapter.class);

    @Override
    public boolean supports(String canal) {
        return "WHATSAPP".equalsIgnoreCase(canal);
    }

    @Override
    public void send(NotificationRequest request) {
        log.info("[WhatsApp simulation] UserId: {} | UserType: {} | Message: {}",
            request.usuarioId(),
            request.usuarioTipo(),
                request.mensaje());
    }
}