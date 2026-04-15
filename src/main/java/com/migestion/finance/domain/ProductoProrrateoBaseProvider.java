package com.migestion.finance.domain;

import java.time.Instant;
import java.util.List;

public interface ProductoProrrateoBaseProvider {

    List<ProductoBaseProrrateo> getBasePorProducto(
            Long tenantId,
            Instant periodoInicio,
            Instant periodoFin
    );
}
