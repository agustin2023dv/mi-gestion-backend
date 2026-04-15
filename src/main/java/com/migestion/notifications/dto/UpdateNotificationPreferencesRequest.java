package com.migestion.notifications.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateNotificationPreferencesRequest(
        Boolean emailEnabled,
        Boolean whatsappEnabled,
        Boolean pushEnabled,
        @Size(max = 50) String whatsappNumero,
        @Size(max = 255) String emailDestino
) {
}
