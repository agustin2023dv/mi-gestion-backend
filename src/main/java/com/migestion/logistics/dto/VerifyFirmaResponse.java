package com.migestion.logistics.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VerifyFirmaResponse(
        UUID token,
        String estado,
        Instant firmadoEn,
        String mensaje
) {
}