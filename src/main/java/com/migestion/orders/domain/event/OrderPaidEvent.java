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
public class OrderPaidEvent {

    private final Long pedidoId;
    private final Long tenantId;
    private final Long pagoId;
    private final String transactionId;
    private final BigDecimal amount;
    private final Instant occurredAt;
}
