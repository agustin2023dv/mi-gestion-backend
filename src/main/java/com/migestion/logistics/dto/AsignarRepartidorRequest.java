package com.migestion.logistics.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AsignarRepartidorRequest(
        @NotNull Long repartidorId
) {
}
