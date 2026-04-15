package com.migestion.marketing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record CreateCuponRequest(
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 20) String tipoDescuento,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal valorDescuento,
        @Positive Integer usosMaximos,
        @NotNull Instant fechaInicio,
        Instant fechaFin,
        @DecimalMin(value = "0.00") BigDecimal montoMinimo,
        Boolean isActive
) {
}
