package com.migestion.marketing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ModerateReviewRequest(
        @NotNull(message = "La acción es requerida")
        @Pattern(regexp = "APPROVE|REJECT", message = "La acción debe ser APPROVE o REJECT")
        String accion,

        @Size(max = 500, message = "El motivo de rechazo no debe exceder 500 caracteres")
        String motivoRechazo
) {
}
