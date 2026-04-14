package com.migestion.catalog.dto;

import lombok.Builder;

@Builder
public record CategoriaReferenceResponse(
        Long id,
        String nombre
) {
}