package com.migestion.notifications.application;

import com.migestion.notifications.domain.ConfiguracionNotificacion;
import com.migestion.notifications.domain.ConfiguracionNotificacionRepository;
import com.migestion.notifications.dto.ConfiguracionNotificacionResponse;
import com.migestion.notifications.dto.UpdateNotificationPreferencesRequest;
import com.migestion.notifications.infrastructure.NotificationMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateNotificationPreferencesUseCase {

    private final ConfiguracionNotificacionRepository configuracionNotificacionRepository;
    private final NotificationMapper notificationMapper;

    public UpdateNotificationPreferencesUseCase(
            ConfiguracionNotificacionRepository configuracionNotificacionRepository,
            NotificationMapper notificationMapper) {
        this.configuracionNotificacionRepository = configuracionNotificacionRepository;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public ConfiguracionNotificacionResponse execute(UpdateNotificationPreferencesRequest request) {
        Long tenantId = requireTenantId();

        ConfiguracionNotificacion configuracion = configuracionNotificacionRepository.findByTenantId(tenantId)
                .orElseGet(() -> ConfiguracionNotificacion.builder()
                        .tenantId(tenantId)
                        .build());

        if (request.emailEnabled() != null) {
            configuracion.setEmailEnabled(request.emailEnabled());
        }
        if (request.whatsappEnabled() != null) {
            configuracion.setWhatsappEnabled(request.whatsappEnabled());
        }
        if (request.pushEnabled() != null) {
            configuracion.setPushEnabled(request.pushEnabled());
        }
        if (request.whatsappNumero() != null) {
            configuracion.setWhatsappNumero(request.whatsappNumero());
        }
        if (request.emailDestino() != null) {
            configuracion.setEmailDestino(request.emailDestino());
        }

        ConfiguracionNotificacion persisted = configuracionNotificacionRepository.save(configuracion);
        return notificationMapper.toResponse(persisted);
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
