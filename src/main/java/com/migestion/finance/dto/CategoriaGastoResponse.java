package com.migestion.finance.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record CategoriaGastoResponse(
        Long id,
        Long tenantId,
        String nombre,
        String tipoNaturaleza,
        boolean esDirecto,
        boolean esProrrateable,
        String descripcion,
        Instant createdAt,
        Instant updatedAt
) {
}
