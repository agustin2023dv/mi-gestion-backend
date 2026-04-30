package com.migestion.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record BookAppointmentRequest(
        @NotNull Long productoId,
        @NotNull LocalDateTime fechaHoraInicio,
        Long empleadoId,
        @Size(max = 500) String notasCliente
) {
}
