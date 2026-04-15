package com.migestion.logistics.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record TarifaDeliveryResponse(
        Long id,
        Long tenantId,
        String nombre,
        String tipoCalculo,
        BigDecimal precioBase,
        BigDecimal precioPorKm,
        BigDecimal distanciaMinimaKm,
        BigDecimal distanciaMaximaKm,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
