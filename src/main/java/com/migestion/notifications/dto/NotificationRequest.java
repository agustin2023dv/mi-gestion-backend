package com.migestion.notifications.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record NotificationRequest(
        @NotNull Long usuarioId,
        @NotBlank @Size(max = 50) String usuarioTipo,
        @NotBlank @Size(max = 50) String canal,
        @Size(max = 200) String titulo,
        @NotBlank @Size(max = 4000) String mensaje,
        String dataJson
) {
}