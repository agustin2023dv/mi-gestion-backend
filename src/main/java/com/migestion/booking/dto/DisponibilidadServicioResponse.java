package com.migestion.booking.dto;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record DisponibilidadServicioResponse(
        Long id,
        Long tenantId,
        Long productoId,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        int cuposMaximosPorTurno,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
