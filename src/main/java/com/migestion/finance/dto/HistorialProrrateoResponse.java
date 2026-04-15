package com.migestion.finance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record HistorialProrrateoResponse(
        Instant fechaCalculo,
        Instant periodoInicio,
        Instant periodoFin,
        Long criterioProrrateoId,
        Long cantidadAsignaciones,
        BigDecimal montoTotalAsignado
) {
}
