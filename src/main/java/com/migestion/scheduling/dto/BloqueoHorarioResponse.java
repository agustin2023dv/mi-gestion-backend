package com.migestion.scheduling.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record BloqueoHorarioResponse(
        Long id,
        Long tenantId,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String motivo,
        String tipoBloqueo,
        boolean isRecurrente,
        String reglaRecurrencia,
        Instant createdAt,
        Instant updatedAt
) {
}
