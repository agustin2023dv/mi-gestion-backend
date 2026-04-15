package com.migestion.marketing.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ReviewResponse(
        Long id,
        Long productoId,
        String productoNombre,
        Long tenantId,
        String tenantNombre,
        Long clienteId,
        String clienteNombre,
        Long pedidoId,
        Long turnoId,
        Integer puntuacion,
        String comentario,
        boolean esVerificada,
        boolean requiereModeracion,
        boolean isPublished,
        String respuestaEmprendedor,
        Instant respuestaFecha,
        String motivoRechazo,
        Instant createdAt,
        Instant updatedAt
) {
}
