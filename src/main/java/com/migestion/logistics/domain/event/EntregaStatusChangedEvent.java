package com.migestion.logistics.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EntregaStatusChangedEvent {

    private final Long entregaId;
    private final Long pedidoId;
    private final Long repartidorId;
    private final Long tenantId;
    private final String nuevoEstado;
    private final BigDecimal latitud;
    private final BigDecimal longitud;
    private final Instant occurredAt;
}
