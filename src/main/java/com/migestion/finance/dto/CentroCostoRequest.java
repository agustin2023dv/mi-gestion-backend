package com.migestion.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CentroCostoRequest(
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 100) String nombre,
        String descripcion,
        Boolean isActive
) {
}
