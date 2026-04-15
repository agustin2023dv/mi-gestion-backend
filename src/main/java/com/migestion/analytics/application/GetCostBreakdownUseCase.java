package com.migestion.analytics.application;

import com.migestion.analytics.dto.CostoBreakdownResponse;
import com.migestion.analytics.dto.DashboardCostoResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCostBreakdownUseCase {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final GetCostDashboardUseCase getCostDashboardUseCase;

    public GetCostBreakdownUseCase(GetCostDashboardUseCase getCostDashboardUseCase) {
        this.getCostDashboardUseCase = getCostDashboardUseCase;
    }

    @Transactional(readOnly = true)
    public CostoBreakdownResponse execute(Long tenantId, String periodo, LocalDate fechaInicio, LocalDate fechaFin) {
        DashboardCostoResponse dashboard = getCostDashboardUseCase.execute(tenantId, periodo, fechaInicio, fechaFin);
        DashboardCostoResponse.Metricas metricas = dashboard.metricas();

        BigDecimal costoIngredientes = defaultValue(metricas.costoIngredientes());
        BigDecimal costoEmpleados = defaultValue(metricas.costoEmpleados());
        BigDecimal costoCifAsignado = defaultValue(metricas.costoCIFAsignado());
        BigDecimal total = costoIngredientes.add(costoEmpleados).add(costoCifAsignado);

        List<CostoBreakdownResponse.CostoItem> costos = List.of(
                CostoBreakdownResponse.CostoItem.builder()
                        .tipo("Ingredientes")
                        .monto(costoIngredientes)
                        .porcentaje(calculatePercentage(costoIngredientes, total))
                        .build(),
                CostoBreakdownResponse.CostoItem.builder()
                        .tipo("Mano de obra")
                        .monto(costoEmpleados)
                        .porcentaje(calculatePercentage(costoEmpleados, total))
                        .build(),
                CostoBreakdownResponse.CostoItem.builder()
                        .tipo("CIF")
                        .monto(costoCifAsignado)
                        .porcentaje(calculatePercentage(costoCifAsignado, total))
                        .build());

        return CostoBreakdownResponse.builder()
                .costos(costos)
                .build();
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private BigDecimal calculatePercentage(BigDecimal value, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.multiply(ONE_HUNDRED).divide(total, 2, RoundingMode.HALF_UP);
    }
}