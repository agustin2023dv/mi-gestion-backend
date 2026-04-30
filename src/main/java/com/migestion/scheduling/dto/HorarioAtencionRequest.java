package com.migestion.scheduling.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record HorarioAtencionRequest(
        @NotNull DayOfWeek diaSemana,
        @NotNull LocalTime horaApertura,
        @NotNull LocalTime horaCierre,
        Boolean isActivo
) {
}
