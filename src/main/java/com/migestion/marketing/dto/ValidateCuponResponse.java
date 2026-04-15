package com.migestion.marketing.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ValidateCuponResponse(
        boolean valido,
        CuponValidationData cupon,
        BigDecimal montoDescuento,
        String motivo
) {

    @Builder
    public record CuponValidationData(
            Long id,
            String codigo,
            String tipoDescuento,
            BigDecimal valorDescuento
    ) {
    }
}
