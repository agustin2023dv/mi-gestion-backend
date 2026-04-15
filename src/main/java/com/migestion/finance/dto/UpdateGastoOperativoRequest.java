package com.migestion.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record UpdateGastoOperativoRequest(
        @Size(max = 150) String nombre,
        @DecimalMin("0.01") BigDecimal monto,
        Instant fechaRegistro,
        Long empleadoId,
        Long centroCostoId,
        Long categoriaGastoId,
        String periodicidad,
        Boolean esRecurrente,
        Boolean esDirecto,
        Boolean esProrrateable,
        String descripcion
) {
}
