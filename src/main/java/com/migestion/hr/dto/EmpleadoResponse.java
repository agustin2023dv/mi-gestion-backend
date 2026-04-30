package com.migestion.hr.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record EmpleadoResponse(
        Long id,
        Long tenantId,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String especialidad,
        String rol,
        String tipoRemuneracion,
        BigDecimal montoSueldoFijo,
        String frecuenciaPago,
        BigDecimal costoHoraBase,
        BigDecimal porcentajeComision,
        LocalDate fechaIngreso,
        LocalDate fechaEgreso,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
