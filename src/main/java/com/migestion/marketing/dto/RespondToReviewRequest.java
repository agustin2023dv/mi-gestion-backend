package com.migestion.marketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RespondToReviewRequest(
        @NotBlank(message = "La respuesta es requerida")
        @Size(max = 500, message = "La respuesta no debe exceder 500 caracteres")
        String respuesta
) {
}
