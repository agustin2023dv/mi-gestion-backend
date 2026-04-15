package com.migestion.analytics.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CategoriaTopResponse(
        Long categoriaId,
        String nombre,
        BigDecimal ingresosGenerados,
        BigDecimal porcentaje
) {
}