package com.migestion.catalog.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record CategoriaListItemResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}