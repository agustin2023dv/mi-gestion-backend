package com.migestion.catalog.dto;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record CategoriaDetailResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean isActive,
        List<SubcategoriaSummaryResponse> subcategorias,
        Instant createdAt,
        Instant updatedAt
) {
}