package com.migestion.notifications.infrastructure;

import com.migestion.notifications.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationAdapter implements ChannelNotificationAdapter {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationAdapter.class);

    @Override
    public boolean supports(String canal) {
        return "EMAIL".equalsIgnoreCase(canal);
    }

    @Override
    public void send(NotificationRequest request) {
        log.info("[Email simulation] To: {} | Subject: {} | Message: {}",
                request.destinatario(),
                request.titulo(),
                request.mensaje());
    }
}