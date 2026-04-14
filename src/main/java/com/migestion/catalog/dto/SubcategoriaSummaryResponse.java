package com.migestion.catalog.dto;

import lombok.Builder;

@Builder
public record SubcategoriaSummaryResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean isActive
) {
}