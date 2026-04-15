package com.migestion.finance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record AsignacionCostoIndirectoResponse(
        Long id,
        Long gastoOperativoId,
        Long criterioProrrateoId,
        Long productoId,
        Long centroCostoId,
        BigDecimal montoAsignado,
        Instant periodoInicio,
        Instant periodoFin,
        Instant fechaCalculo,
        String notas
) {
}
