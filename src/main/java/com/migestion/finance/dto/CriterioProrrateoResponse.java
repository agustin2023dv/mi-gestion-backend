package com.migestion.finance.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record CriterioProrrateoResponse(
        Long id,
        Long tenantId,
        String nombre,
        String tipo,
        String formula,
        String parametrosJson,
        String descripcion,
        boolean isDefault,
        Instant createdAt,
        Instant updatedAt
) {
}
