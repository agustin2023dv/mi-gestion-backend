package com.migestion.finance.domain;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ProductoBaseProrrateo(
        Long productoId,
        BigDecimal unidadesVendidas,
        BigDecimal ingresosGenerados
) {
}
