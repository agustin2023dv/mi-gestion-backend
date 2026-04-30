package com.migestion.scheduling.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record BloqueoHorarioRequest(
        @NotNull LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin,
        @Size(max = 255) String motivo,
        @Size(max = 50) String tipoBloqueo,
        Boolean isRecurrente,
        @Size(max = 255) String reglaRecurrencia
) {
}
