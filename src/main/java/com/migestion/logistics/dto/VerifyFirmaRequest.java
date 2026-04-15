package com.migestion.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Builder;

@Builder
public record VerifyFirmaRequest(
        @NotBlank String firmaDatos,
        Map<String, Object> dispositivoInfo
) {
}
