package com.migestion.logistics.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FirmaResponse(
        UUID token,
        String qrCodeData,
        String qrCodeUrl,
        Instant expiracion
) {
}
