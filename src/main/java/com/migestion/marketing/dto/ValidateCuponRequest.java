package com.migestion.marketing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ValidateCuponRequest(
        @NotBlank @Size(max = 50) String codigo,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal montoPedido
) {
}
