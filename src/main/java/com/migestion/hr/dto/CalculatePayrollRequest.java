package com.migestion.hr.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CalculatePayrollRequest(
        @NotNull Long empleadoId,
        @NotNull LocalDate periodoInicio,
        @NotNull LocalDate periodoFin,
        @Positive Integer diasTrabajados,
        @PositiveOrZero BigDecimal horasTrabajadas,
        @PositiveOrZero BigDecimal bonificaciones,
        @PositiveOrZero BigDecimal descuentos
) {
}
