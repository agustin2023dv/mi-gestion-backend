package com.migestion.hr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CreateEmpleadoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8) String password,
        @Size(max = 20) String telefono,
        @Size(max = 100) String especialidad,
        @Size(max = 50) String rol,
        @Size(max = 50) String tipoRemuneracion,
        @PositiveOrZero BigDecimal montoSueldoFijo,
        @Size(max = 20) String frecuenciaPago,
        @PositiveOrZero BigDecimal costoHoraBase,
        @PositiveOrZero BigDecimal porcentajeComision,
        LocalDate fechaIngreso
) {
}
