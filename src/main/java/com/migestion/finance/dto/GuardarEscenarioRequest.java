package com.migestion.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record GuardarEscenarioRequest(
        @NotBlank @Size(max = 100) String nombreEscenario,
        @PositiveOrZero BigDecimal precioPromedioSimulado,
        @PositiveOrZero BigDecimal costoVariableSimulado,
        @PositiveOrZero BigDecimal costosEmpleadosSimulado,
        @PositiveOrZero BigDecimal costosIngredientesSimulado,
        @PositiveOrZero BigDecimal gastosFijosSimulado
) {
}
