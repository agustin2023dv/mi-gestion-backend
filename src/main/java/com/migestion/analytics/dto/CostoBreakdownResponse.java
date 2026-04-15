package com.migestion.analytics.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record CostoBreakdownResponse(
        List<CostoItem> costos
) {

    @Builder
    public record CostoItem(
            String tipo,
            BigDecimal monto,
            BigDecimal porcentaje
    ) {
    }
}