package com.migestion.finance.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ConfiguracionContabilidadCostosRequest(
        Boolean incluyeManoObraDirectaEnCosto,
        Boolean incluyeCifEnCosto,
        Boolean incluyeImpuestosEnCosto,
        @Size(max = 50) @Pattern(regexp = "^(peps|ueps|promedio_ponderado)$") String metodoValoracionInventario,
        @Size(min = 3, max = 10) @Pattern(regexp = "^[A-Z]{3,10}$") String monedaFuncional
) {
}
