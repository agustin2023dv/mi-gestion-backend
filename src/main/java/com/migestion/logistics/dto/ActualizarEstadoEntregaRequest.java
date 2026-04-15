package com.migestion.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ActualizarEstadoEntregaRequest(
        @NotBlank @Size(max = 50) String nuevoEstado,
        BigDecimal latitud,
        BigDecimal longitud,
        @Size(max = 20) String codigoEntrega
) {
}
