package com.migestion.logistics.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record TarifaDeliveryPatchRequest(
        @Size(max = 100) String nombre,
        @Size(max = 50) String tipoCalculo,
        @PositiveOrZero BigDecimal precioBase,
        @PositiveOrZero BigDecimal precioPorKm,
        @PositiveOrZero BigDecimal distanciaMinimaKm,
        @PositiveOrZero BigDecimal distanciaMaximaKm,
        Boolean isActive
) {
}
