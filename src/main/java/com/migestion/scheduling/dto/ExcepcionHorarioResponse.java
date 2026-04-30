package com.migestion.scheduling.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record ExcepcionHorarioResponse(
        Long id,
        Long tenantId,
        LocalDate fecha,
        LocalTime horaApertura,
        LocalTime horaCierre,
        boolean isCerradoCompleto,
        String motivo,
        boolean afectaBooking,
        boolean afectaPedidos,
        Instant createdAt,
        Instant updatedAt
) {
}
