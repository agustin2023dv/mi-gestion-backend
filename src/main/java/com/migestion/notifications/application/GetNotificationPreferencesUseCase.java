package com.migestion.notifications.application;

import com.migestion.notifications.domain.ConfiguracionNotificacion;
import com.migestion.notifications.domain.ConfiguracionNotificacionRepository;
import com.migestion.notifications.dto.ConfiguracionNotificacionResponse;
import com.migestion.notifications.infrastructure.NotificationMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationPreferencesUseCase {

    private final ConfiguracionNotificacionRepository configuracionNotificacionRepository;
    private final NotificationMapper notificationMapper;

    public GetNotificationPreferencesUseCase(
            ConfiguracionNotificacionRepository configuracionNotificacionRepository,
            NotificationMapper notificationMapper) {
        this.configuracionNotificacionRepository = configuracionNotificacionRepository;
        this.notificationMapper = notificationMapper;
    }

    @Transactional(readOnly = true)
    public ConfiguracionNotificacionResponse execute() {
        Long tenantId = requireTenantId();

        ConfiguracionNotificacion configuracion = configuracionNotificacionRepository.findByTenantId(tenantId)
                .orElseGet(() -> ConfiguracionNotificacion.builder()
                        .tenantId(tenantId)
                        .build());

        return notificationMapper.toResponse(configuracion);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }
        return tenantId;
    }
}