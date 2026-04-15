package com.migestion.orders.domain.event;

import java.math.BigDecimal;
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
    private final BigDecimal monto;
    private final String transactionId;
}
