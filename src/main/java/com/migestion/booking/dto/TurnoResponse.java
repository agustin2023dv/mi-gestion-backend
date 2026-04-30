package com.migestion.booking.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TurnoResponse(
        Long id,
        Long tenantId,
        Long clienteId,
        Long productoId,
        Long empleadoId,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        String estado,
        String tipoConfirmacion,
        String codigoConfirmacion,
        String notasCliente,
        String notasTenant,
        BigDecimal precioAplicado,
        String estadoPago,
        boolean esCancelado,
        String motivoCancelacion,
        Instant createdAt,
        Instant updatedAt
) {
}
