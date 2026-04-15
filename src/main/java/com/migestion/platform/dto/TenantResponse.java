package com.migestion.platform.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record TenantResponse(
        Long id,
        String tenantId,
        String nombreNegocio,
        String slug,
        PlanSuscripcionResponse planSuscripcion,
        PropietarioResponse propietario,
        String logoUrl,
        String colorPrimario,
        String colorSecundario,
        boolean visibilidadPublica,
        boolean aceptaReservasServicios,
        boolean permitePedidosProgramados,
        Boolean isActive,
        Boolean isSuspended,
        Instant createdAt,
        Instant updatedAt
) {

    @Builder
    public record PropietarioResponse(
            Long id,
            String nombre,
            String apellido,
            String email,
            String telefono
    ) {
    }
}
