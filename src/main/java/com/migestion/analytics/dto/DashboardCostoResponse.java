package com.migestion.analytics.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record DashboardCostoResponse(
        String periodo,
        Metricas metricas
) {

    @Builder
    public record Metricas(
            BigDecimal ingresosTotales,
            BigDecimal costoIngredientes,
            BigDecimal costoEmpleados,
            BigDecimal costoCIFAsignado,
            BigDecimal costoTotal,
            BigDecimal margenBruto,
            BigDecimal margenPorcentual,
            BigDecimal gastosFijosTotales,
            BigDecimal margenNeto,
            BigDecimal bepUnidades,
            BigDecimal bepMonto
    ) {
    }
}