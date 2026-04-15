package com.migestion.notifications.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record NotificacionResponse(
        Long id,
        String tipo,
        String canal,
        String titulo,
        String mensaje,
        String dataJson,
        boolean leido,
        Instant enviadoEn,
        Instant createdAt
) {
}