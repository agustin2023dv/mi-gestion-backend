package com.migestion.scheduling.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record ExcepcionHorarioRequest(
        @NotNull LocalDate fecha,
        LocalTime horaApertura,
        LocalTime horaCierre,
        Boolean isCerradoCompleto,
        @Size(max = 255) String motivo,
        Boolean afectaBooking,
        Boolean afectaPedidos
) {
}
