package com.migestion.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record NotificationRequest(
        @NotBlank @Size(max = 50) String canal,
        @NotBlank @Size(max = 255) String destinatario,
        @Size(max = 200) String titulo,
        @NotBlank @Size(max = 4000) String mensaje
) {
}