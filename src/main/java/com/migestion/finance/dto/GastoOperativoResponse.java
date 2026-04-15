package com.migestion.finance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record GastoOperativoResponse(
        Long id,
        Long tenantId,
        String nombre,
        BigDecimal monto,
        Instant fechaRegistro,
        Long empleadoId,
        Long centroCostoId,
        Long categoriaGastoId,
        String periodicidad,
        boolean esRecurrente,
        boolean esDirecto,
        boolean esProrrateable,
        String descripcion,
        Instant createdAt,
        Instant updatedAt
) {
}
