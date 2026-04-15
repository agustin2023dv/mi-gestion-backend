package com.migestion.logistics.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record GenerateFirmaRequest(
        @NotNull @Positive Long entregaId
) {
}
