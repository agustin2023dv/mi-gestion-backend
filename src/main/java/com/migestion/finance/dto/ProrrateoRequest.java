package com.migestion.finance.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ProrrateoRequest(
        @NotNull Instant periodoInicio,
        @NotNull Instant periodoFin,
        Long criterioId
) {
}
