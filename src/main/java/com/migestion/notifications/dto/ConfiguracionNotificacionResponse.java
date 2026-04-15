package com.migestion.notifications.dto;

import lombok.Builder;

@Builder
public record ConfiguracionNotificacionResponse(
        Long id,
        boolean emailEnabled,
        boolean whatsappEnabled,
        boolean pushEnabled,
        String whatsappNumero,
        String emailDestino
) {
}