package com.migestion.logistics.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record FirmaSessionResponse(
        UUID token,
        String estado,
        PedidoInfo pedido,
        RepartidorInfo repartidor,
        Instant generadoEn,
        Instant expiracion
) {

    @Builder
    public record PedidoInfo(
            Long id,
            String numeroPedido,
            BigDecimal total,
            String tipoEntrega,
            String metodoPago,
            Instant fechaPedido
    ) {
    }

    @Builder
    public record RepartidorInfo(
            Long id,
            String nombre,
            String apellido,
            String telefono
    ) {
    }
}