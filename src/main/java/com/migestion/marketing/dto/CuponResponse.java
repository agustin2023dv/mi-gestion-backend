package com.migestion.marketing.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record CuponResponse(
        Long id,
        String codigo,
        String tipoDescuento,
        BigDecimal valorDescuento,
        Integer usosMaximos,
        Integer usosActuales,
        Instant fechaInicio,
        Instant fechaFin,
        BigDecimal montoMinimo,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
