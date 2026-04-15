package com.migestion.logistics.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FirmaEntregaResponse(
        UUID token,
        String estado,
        String firmaDatos,
        Map<String, Object> dispositivoInfo,
        Instant generadoEn,
        Instant firmadoEn
) {
}