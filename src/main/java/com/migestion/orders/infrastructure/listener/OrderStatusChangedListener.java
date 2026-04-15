package com.migestion.orders.infrastructure.listener;

import com.migestion.notifications.application.SendNotificationUseCase;
import com.migestion.notifications.dto.NotificationRequest;
import com.migestion.orders.domain.event.OrderStatusChangedEvent;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class OrderStatusChangedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusChangedListener.class);
    private static final String PUSH_CHANNEL = "PUSH";
    private static final String CLIENT_ROLE = "CLIENTE";
    private static final List<String> SKIPPED_STATUS_CODES = List.of("CONFIRMED", "CONFIRMADO");

    private final SendNotificationUseCase sendNotificationUseCase;

    public OrderStatusChangedListener(SendNotificationUseCase sendNotificationUseCase) {
        this.sendNotificationUseCase = sendNotificationUseCase;
    }

    @EventListener
    @Transactional
    public void handle(OrderStatusChangedEvent event) {
        if (event.getClienteId() == null) {
            return;
        }

        if (isSkippedStatus(event.getNuevoEstado())) {
            return;
        }

        runInTenantContext(event.getTenantId(), () -> {
            try {
                sendNotificationUseCase.execute(NotificationRequest.builder()
                        .usuarioId(event.getClienteId())
                        .usuarioTipo(CLIENT_ROLE)
                        .canal(PUSH_CHANNEL)
                        .titulo("Estado de pedido actualizado")
                        .mensaje("Tu pedido #" + event.getNumeroPedido()
                                + " ahora esta en estado " + normalizeStatus(event.getNuevoEstado()))
                        .build());
            } catch (Exception ex) {
                log.warn(
                        "Failed to send status-change notification for pedidoId={}, tenantId={}, status={}",
                        event.getPedidoId(),
                        event.getTenantId(),
                        event.getNuevoEstado(),
                        ex
                );
            }
        });
    }

    private boolean isSkippedStatus(String status) {
        return StringUtils.hasText(status)
                && SKIPPED_STATUS_CODES.stream().anyMatch(code -> code.equalsIgnoreCase(status));
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "DESCONOCIDO";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private void runInTenantContext(Long tenantId, Runnable callback) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            callback.run();
        } finally {
            if (previousTenantId == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(previousTenantId);
            }
        }
    }
}
