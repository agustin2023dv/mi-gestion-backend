package com.migestion.finance.domain;

import java.time.Instant;
import java.util.List;

public interface ProrrateoService {

    List<AsignacionCostoIndirecto> calcularProrrateo(
            Long tenantId,
            Instant periodoInicio,
            Instant periodoFin,
            Long criterioId
    );
}
