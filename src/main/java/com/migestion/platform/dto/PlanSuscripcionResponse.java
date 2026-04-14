package com.migestion.platform.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record PlanSuscripcionResponse(
        Long id,
        String nombre,
        Integer maxProductos,
        Integer maxPedidosMensuales,
        Integer maxAlmacenamientoMb,
        BigDecimal precioMensual,
        List<String> features,
        Integer orderLevel
) {
}
