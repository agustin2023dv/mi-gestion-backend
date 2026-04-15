package com.migestion.finance.dto;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record SimuladorCalculoRequest(
        @PositiveOrZero BigDecimal precioPromedioSimulado,
        @PositiveOrZero BigDecimal costoVariableSimulado,
        @PositiveOrZero BigDecimal costosEmpleadosSimulado,
        @PositiveOrZero BigDecimal costosIngredientesSimulado,
        @PositiveOrZero BigDecimal gastosFijosSimulado
) {
}
