package com.migestion.scheduling.dto;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record HorarioAtencionResponse(
        Long id,
        Long tenantId,
        DayOfWeek diaSemana,
        LocalTime horaApertura,
        LocalTime horaCierre,
        boolean isActivo,
        Instant createdAt,
        Instant updatedAt
) {
}
