package com.migestion.catalog.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ProductoResponse(
        Long id,
        Long tenantId,
        Long subcategoriaId,
        String nombre,
        String descripcion,
        BigDecimal precio,
        BigDecimal costoUnitarioCalculado,
        BigDecimal costoUnitarioManualOverride,
        boolean usaCostoCalculado,
        Integer stock,
        Integer stockMinimo,
        String imagenUrl,
        String sku,
        boolean esPersonalizable,
        boolean esServicio,
        Integer duracionMinutos,
        boolean permiteBooking,
        Integer bufferEntreTurnosMin,
        boolean requiereVerificacionEdad,
        Integer edadMinima,
        boolean requiereEmpleadoEspecifico,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}