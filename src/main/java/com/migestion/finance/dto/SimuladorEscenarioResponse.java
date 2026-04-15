package com.migestion.finance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record SimuladorEscenarioResponse(
        Long id,
        Long tenantId,
        String nombreEscenario,
        BigDecimal precioPromedioSimulado,
        BigDecimal costoVariableSimulado,
        BigDecimal costosEmpleadosSimulado,
        BigDecimal costosIngredientesSimulado,
        BigDecimal gastosFijosSimulado,
        BigDecimal margenSimulado,
        BigDecimal bepSimulado,
        boolean isSaved,
        Instant createdAt,
        Instant updatedAt
) {
}
