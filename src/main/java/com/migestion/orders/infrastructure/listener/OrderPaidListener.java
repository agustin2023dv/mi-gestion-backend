package com.migestion.orders.infrastructure.listener;

import com.migestion.orders.domain.EstadoPedido;
import com.migestion.orders.domain.EstadoPedidoRepository;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoEstadoHistorial;
import com.migestion.orders.domain.PedidoEstadoHistorialRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.domain.event.OrderStatusChangedEvent;
import com.migestion.orders.domain.event.OrderPaidEvent;
import com.migestion.notifications.application.SendNotificationUseCase;
import com.migestion.notifications.dto.NotificationRequest;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class OrderPaidListener {

    private static final Logger log = LoggerFactory.getLogger(OrderPaidListener.class);
    private static final List<String> PENDING_CODES = List.of("PENDING", "PENDIENTE", "pending", "pendiente");
    private static final List<String> CONFIRMED_CODES = List.of("CONFIRMED", "CONFIRMADO", "confirmed", "confirmado");
    private static final String PUSH_CHANNEL = "PUSH";
    private static final String CLIENT_ROLE = "CLIENTE";
    private static final String FALLBACK_ENTREPRENEUR_ROLE = "ADMIN";

    private final PedidoRepository pedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final PedidoEstadoHistorialRepository pedidoEstadoHistorialRepository;
    private final SendNotificationUseCase sendNotificationUseCase;
    private final TenantRepository tenantRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderPaidListener(
            PedidoRepository pedidoRepository,
            EstadoPedidoRepository estadoPedidoRepository,
            PedidoEstadoHistorialRepository pedidoEstadoHistorialRepository,
            SendNotificationUseCase sendNotificationUseCase,
            TenantRepository tenantRepository,
            UsuarioTenantRepository usuarioTenantRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.pedidoRepository = pedidoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.pedidoEstadoHistorialRepository = pedidoEstadoHistorialRepository;
        this.sendNotificationUseCase = sendNotificationUseCase;
        this.tenantRepository = tenantRepository;
        this.usuarioTenantRepository = usuarioTenantRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    @Transactional
    public void handle(OrderPaidEvent event) {
        Pedido pedido = pedidoRepository.findByIdAndTenantId(event.getPedidoId(), event.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", event.getPedidoId()));

        pedido.setEstadoPago("PAID");

        if (isPendingState(pedido.getEstado())) {
            EstadoPedido confirmedState = resolveConfirmedOrderState();
            pedido.setEstado(confirmedState);
            pedidoEstadoHistorialRepository.save(PedidoEstadoHistorial.builder()
                    .pedidoId(pedido.getId())
                    .estado(confirmedState)
                    .fechaCambio(Instant.now())
                    .notas(buildHistoryNote(event.getTransactionId()))
                    .build());

                applicationEventPublisher.publishEvent(
                    OrderStatusChangedEvent.builder()
                        .pedidoId(pedido.getId())
                        .tenantId(pedido.getTenantId())
                        .clienteId(pedido.getClienteId())
                        .numeroPedido(pedido.getNumeroPedido())
                        .nuevoEstado(confirmedState.getCodigo())
                        .occurredAt(Instant.now())
                        .build()
                );
        }

        pedidoRepository.save(pedido);
            notifyOrderPaid(pedido, event);

        log.info(
                "Order payment confirmed for pedidoId={}, tenantId={}, transactionId={}, monto={}, estadoPago={}, estado={}",
                pedido.getId(),
                pedido.getTenantId(),
                event.getTransactionId(),
                event.getMonto(),
                pedido.getEstadoPago(),
                pedido.getEstado().getCodigo()
        );
    }

    private void notifyOrderPaid(Pedido pedido, OrderPaidEvent event) {
        runInTenantContext(pedido.getTenantId(), () -> {
            sendClientPaidNotification(pedido);
            sendEntrepreneurPaidNotification(pedido, event);
        });
    }

    private void sendClientPaidNotification(Pedido pedido) {
        if (pedido.getClienteId() == null) {
            return;
        }

        try {
            sendNotificationUseCase.execute(NotificationRequest.builder()
                    .usuarioId(pedido.getClienteId())
                    .usuarioTipo(CLIENT_ROLE)
                    .canal(PUSH_CHANNEL)
                    .titulo("Pedido confirmado")
                    .mensaje("Tu pedido #" + pedido.getNumeroPedido() + " ha sido confirmado")
                    .build());
        } catch (Exception ex) {
            log.warn(
                    "Failed to send paid notification to client for pedidoId={}, tenantId={}",
                    pedido.getId(),
                    pedido.getTenantId(),
                    ex
            );
        }
    }

    private void sendEntrepreneurPaidNotification(Pedido pedido, OrderPaidEvent event) {
        Optional<UsuarioTenant> entrepreneur = findTenantOwner(pedido.getTenantId());
        if (entrepreneur.isEmpty()) {
            return;
        }

        UsuarioTenant owner = entrepreneur.get();
        String ownerRole = normalizeUserType(owner.getRol());
        String amount = resolvePaidAmount(event, pedido);

        try {
            sendNotificationUseCase.execute(NotificationRequest.builder()
                    .usuarioId(owner.getId())
                    .usuarioTipo(ownerRole)
                    .canal(PUSH_CHANNEL)
                    .titulo("Nuevo pedido pagado")
                    .mensaje("Nuevo pedido #" + pedido.getNumeroPedido() + " por $" + amount)
                    .build());
        } catch (Exception ex) {
            log.warn(
                    "Failed to send paid notification to entrepreneur for pedidoId={}, tenantId={}",
                    pedido.getId(),
                    pedido.getTenantId(),
                    ex
            );
        }
    }

    private Optional<UsuarioTenant> findTenantOwner(Long tenantId) {
        return tenantRepository.findById(tenantId)
                .map(Tenant::getPropietarioId)
                .filter(ownerId -> ownerId != null && ownerId > 0)
                .flatMap(usuarioTenantRepository::findById);
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

    private String resolvePaidAmount(OrderPaidEvent event, Pedido pedido) {
        if (event.getMonto() != null) {
            return event.getMonto().setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return pedido.getTotal().setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String normalizeUserType(String userType) {
        if (!StringUtils.hasText(userType)) {
            return FALLBACK_ENTREPRENEUR_ROLE;
        }
        return userType.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isPendingState(EstadoPedido estadoPedido) {
        return estadoPedido != null
                && StringUtils.hasText(estadoPedido.getCodigo())
                && PENDING_CODES.stream().anyMatch(code -> code.equalsIgnoreCase(estadoPedido.getCodigo()));
    }

    private EstadoPedido resolveConfirmedOrderState() {
        for (String code : CONFIRMED_CODES) {
            EstadoPedido estado = estadoPedidoRepository.findByCodigo(code).orElse(null);
            if (estado != null) {
                return estado;
            }
        }

        throw new BusinessRuleViolationException(
                "ORDER_CONFIRMED_STATE_NOT_FOUND",
                "Could not find a confirmed order state (CONFIRMED/CONFIRMADO)"
        );
    }

    private String buildHistoryNote(String transactionId) {
        if (!StringUtils.hasText(transactionId)) {
            return "Pedido confirmado tras la acreditacion del pago";
        }
        return "Pedido confirmado tras la acreditacion del pago " + transactionId;
    }
}
