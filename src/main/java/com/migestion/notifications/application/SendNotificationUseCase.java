package com.migestion.notifications.application;

import com.migestion.notifications.domain.Notificacion;
import com.migestion.notifications.domain.NotificacionRepository;
import com.migestion.notifications.dto.NotificacionResponse;
import com.migestion.notifications.dto.NotificationRequest;
import com.migestion.notifications.infrastructure.NotificationMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SendNotificationUseCase {

    private final NotificationSender notificationSender;
    private final NotificacionRepository notificacionRepository;
    private final NotificationMapper notificationMapper;

    public SendNotificationUseCase(
            NotificationSender notificationSender,
            NotificacionRepository notificacionRepository,
            NotificationMapper notificationMapper) {
        this.notificationSender = notificationSender;
        this.notificacionRepository = notificacionRepository;
        this.notificationMapper = notificationMapper;
    }

    @Transactional
    public NotificacionResponse execute(NotificationRequest request) {
        Long tenantId = requireTenantId();

        Notificacion notificacion = Notificacion.builder()
                .tenantId(tenantId)
                .usuarioId(request.usuarioId())
                .usuarioTipo(request.usuarioTipo())
                .tipo("MANUAL")
                .canal(request.canal())
                .titulo(request.titulo())
                .mensaje(request.mensaje())
                .dataJson(request.dataJson())
                .leido(false)
                .enviadoEn(Instant.now())
                .build();

        Notificacion persisted = notificacionRepository.save(notificacion);
        notificationSender.send(request);
        return notificationMapper.toResponse(persisted);
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
