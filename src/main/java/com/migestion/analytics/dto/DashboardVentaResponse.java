package com.migestion.analytics.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record DashboardVentaResponse(
        String periodo,
        Metricas metricas
) {

    @Builder
    public record Metricas(
            BigDecimal ingresosTotales,
            Long pedidosTotales,
            BigDecimal ticketPromedio,
            Long productosVendidos,
            BigDecimal tasaCrecimiento
    ) {
    }
}