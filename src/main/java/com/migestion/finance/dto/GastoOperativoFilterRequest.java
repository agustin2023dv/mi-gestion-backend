package com.migestion.finance.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record GastoOperativoFilterRequest(
        Instant fechaDesde,
        Instant fechaHasta,
        Long categoriaGastoId,
        Long centroCostoId,
        Boolean esProrrateable
) {
}
