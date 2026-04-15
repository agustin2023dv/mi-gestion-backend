package com.migestion.analytics.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ProductoTopResponse(
        Long productoId,
        String nombre,
        Long cantidadVendida,
        BigDecimal ingresosGenerados
) {
}