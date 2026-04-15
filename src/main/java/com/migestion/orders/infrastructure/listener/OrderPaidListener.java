package com.migestion.orders.infrastructure.listener;

import com.migestion.orders.domain.EstadoPedido;
import com.migestion.orders.domain.EstadoPedidoRepository;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoEstadoHistorial;
import com.migestion.orders.domain.PedidoEstadoHistorialRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.domain.event.OrderPaidEvent;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.List;
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

    private final PedidoRepository pedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final PedidoEstadoHistorialRepository pedidoEstadoHistorialRepository;

    public OrderPaidListener(
            PedidoRepository pedidoRepository,
            EstadoPedidoRepository estadoPedidoRepository,
            PedidoEstadoHistorialRepository pedidoEstadoHistorialRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.pedidoEstadoHistorialRepository = pedidoEstadoHistorialRepository;
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
        }

        pedidoRepository.save(pedido);

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
