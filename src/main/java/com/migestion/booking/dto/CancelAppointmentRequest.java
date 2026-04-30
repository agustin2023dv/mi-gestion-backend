package com.migestion.booking.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CancelAppointmentRequest(
        @Size(max = 500) String motivoCancelacion
) {
}
