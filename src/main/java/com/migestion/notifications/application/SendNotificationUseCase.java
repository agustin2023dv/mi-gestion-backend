package com.migestion.notifications.application;

import com.migestion.notifications.dto.NotificationRequest;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SendNotificationUseCase {

    private final NotificationSender notificationSender;

    public SendNotificationUseCase(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Transactional
    public void execute(NotificationRequest request) {
        requireTenantId();
        notificationSender.send(request);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required to send notifications");
        }
        return tenantId;
    }
}
