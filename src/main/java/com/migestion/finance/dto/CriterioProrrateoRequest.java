package com.migestion.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CriterioProrrateoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 50) String tipo,
        String formula,
        String parametrosJson,
        String descripcion,
        Boolean isDefault
) {
}
