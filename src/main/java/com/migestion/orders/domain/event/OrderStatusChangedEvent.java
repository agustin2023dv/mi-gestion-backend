package com.migestion.orders.domain.event;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderStatusChangedEvent {

    private final Long pedidoId;
    private final Long tenantId;
    private final Long clienteId;
    private final String numeroPedido;
    private final String nuevoEstado;
    private final Instant occurredAt;
}
