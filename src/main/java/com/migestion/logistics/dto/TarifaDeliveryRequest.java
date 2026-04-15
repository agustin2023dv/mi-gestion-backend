package com.migestion.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record TarifaDeliveryRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 50) String tipoCalculo,
        @NotNull @PositiveOrZero BigDecimal precioBase,
        @NotNull @PositiveOrZero BigDecimal precioPorKm,
        @PositiveOrZero BigDecimal distanciaMinimaKm,
        @PositiveOrZero BigDecimal distanciaMaximaKm,
        Boolean isActive
) {
}
