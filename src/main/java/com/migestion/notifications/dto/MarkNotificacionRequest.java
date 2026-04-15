package com.migestion.notifications.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MarkNotificacionRequest(
        @NotNull Boolean leido
) {
}
