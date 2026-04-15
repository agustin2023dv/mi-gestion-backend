package com.migestion.marketing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record UpdateCuponRequest(
        @Size(max = 50) String codigo,
        @Size(max = 20) String tipoDescuento,
        @DecimalMin(value = "0.00", inclusive = false) BigDecimal valorDescuento,
        @Positive Integer usosMaximos,
        Instant fechaInicio,
        Instant fechaFin,
        @DecimalMin(value = "0.00") BigDecimal montoMinimo,
        Boolean isActive
) {
}
