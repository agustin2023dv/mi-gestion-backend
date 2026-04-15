package com.migestion.notifications.application;

import com.migestion.notifications.dto.NotificationRequest;

public interface NotificationSender {

    void send(NotificationRequest request);
}