package com.migestion.notifications.infrastructure;

import com.migestion.notifications.application.NotificationSender;
import com.migestion.notifications.dto.NotificationRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationSenderStrategy implements NotificationSender {

    private final List<ChannelNotificationAdapter> adapters;

    public NotificationSenderStrategy(List<ChannelNotificationAdapter> adapters) {
        this.adapters = adapters;
    }

    @Override
    public void send(NotificationRequest request) {
        ChannelNotificationAdapter adapter = adapters.stream()
                .filter(candidate -> candidate.supports(request.canal()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported notification channel: " + request.canal()));

        adapter.send(request);
    }
}