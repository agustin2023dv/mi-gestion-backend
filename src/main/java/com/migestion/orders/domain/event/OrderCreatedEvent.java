package com.migestion.orders.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreatedEvent {

    private final Long pedidoId;
    private final Long tenantId;
    private final String numeroPedido;
    private final String trackingToken;
    private final BigDecimal total;
    private final Instant occurredAt;
}
