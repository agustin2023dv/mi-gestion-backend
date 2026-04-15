package com.migestion.finance.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record CentroCostoResponse(
        Long id,
        Long tenantId,
        String codigo,
        String nombre,
        String descripcion,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
