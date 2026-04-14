package com.migestion.catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record UpdateProductoRequest(
        @Size(max = 150) String nombre,
        @Size(max = 2000) String descripcion,
        @Positive BigDecimal precio,
        @PositiveOrZero BigDecimal costoUnitarioManualOverride,
        Boolean usaCostoCalculado,
        @PositiveOrZero Integer stock,
        @PositiveOrZero Integer stockMinimo,
        @Size(max = 2048) String imagenUrl,
        @Size(max = 100) String sku,
        Boolean esPersonalizable,
        Boolean esServicio,
        @Positive Integer duracionMinutos,
        Boolean permiteBooking,
        @PositiveOrZero Integer bufferEntreTurnosMin,
        Boolean requiereVerificacionEdad,
        @Min(0) @Max(120) Integer edadMinima,
        Boolean requiereEmpleadoEspecifico,
        Boolean isActive,
        Long subcategoriaId
) {
}