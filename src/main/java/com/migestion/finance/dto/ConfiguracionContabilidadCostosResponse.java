package com.migestion.finance.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ConfiguracionContabilidadCostosResponse(
        Long id,
        Long tenantId,
        boolean incluyeManoObraDirectaEnCosto,
        boolean incluyeCifEnCosto,
        boolean incluyeImpuestosEnCosto,
        String metodoValoracionInventario,
        String monedaFuncional,
        Instant createdAt,
        Instant updatedAt
) {
}
