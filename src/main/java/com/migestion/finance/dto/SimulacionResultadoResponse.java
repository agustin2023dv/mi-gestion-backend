package com.migestion.finance.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record SimulacionResultadoResponse(
        BigDecimal precioPromedioSimulado,
        BigDecimal costoVariableSimulado,
        BigDecimal costosEmpleadosSimulado,
        BigDecimal costosIngredientesSimulado,
        BigDecimal gastosFijosSimulado,
        BigDecimal margenSimulado,
        BigDecimal bepSimulado,
        BigDecimal ingresosNecesarios
) {
}
