package com.migestion.marketing.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateTenantReviewRequest(
        @NotNull(message = "El ID del pedido es requerido")
        Long pedidoId,

        Long turnoId,

        @NotNull(message = "La puntuación es requerida")
        @Min(value = 1, message = "La puntuación debe estar entre 1 y 5")
        @Max(value = 5, message = "La puntuación debe estar entre 1 y 5")
        Integer puntuacion,

        @Size(max = 1000, message = "El comentario no debe exceder 1000 caracteres")
        String comentario
) {
}
