package com.migestion.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CategoriaGastoRequest(
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 50) String tipoNaturaleza,
        Boolean esDirecto,
        Boolean esProrrateable,
        String descripcion
) {
}
