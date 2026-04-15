package com.migestion.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record CreateGastoOperativoRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotNull @DecimalMin("0.01") BigDecimal monto,
        @NotNull Instant fechaRegistro,
        Long empleadoId,
        Long centroCostoId,
        @NotNull Long categoriaGastoId,
        String periodicidad,
        boolean esRecurrente,
        boolean esDirecto,
        boolean esProrrateable,
        String descripcion
) {
}
