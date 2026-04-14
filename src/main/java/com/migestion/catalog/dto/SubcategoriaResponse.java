package com.migestion.catalog.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record SubcategoriaResponse(
        Long id,
        CategoriaReferenceResponse categoria,
        String nombre,
        String descripcion,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}