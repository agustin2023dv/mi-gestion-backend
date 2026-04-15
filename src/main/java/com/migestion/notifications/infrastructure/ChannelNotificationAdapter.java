package com.migestion.notifications.infrastructure;

import com.migestion.notifications.dto.NotificationRequest;

public interface ChannelNotificationAdapter {

    boolean supports(String canal);

    void send(NotificationRequest request);
}