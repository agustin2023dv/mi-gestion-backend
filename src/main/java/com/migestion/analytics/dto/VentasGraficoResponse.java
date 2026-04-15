package com.migestion.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record VentasGraficoResponse(
        List<Serie> series
) {

    @Builder
    public record Serie(
            LocalDate fecha,
            BigDecimal ingresos,
            Long pedidos
    ) {
    }
}