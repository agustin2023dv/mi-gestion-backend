package com.migestion.hr.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record NominaEmpleadoResponse(
        Long id,
        Long tenantId,
        Long empleadoId,
        LocalDate periodoInicio,
        LocalDate periodoFin,
        Integer diasTrabajados,
        BigDecimal horasTrabajadas,
        BigDecimal sueldoBaseCalculado,
        BigDecimal comisionesGeneradas,
        BigDecimal bonificaciones,
        BigDecimal descuentos,
        BigDecimal totalAPagar,
        boolean isPagado,
        LocalDate fechaPago,
        String estado,
        Instant createdAt,
        Instant updatedAt
) {
}
