package com.migestion.orders.dto;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record PedidoTrackingResponse(
        String numeroPedido,
        String estadoActual,
        String codigoEntrega,
        List<TrackingEstadoResponse> historial,
        RepartidorTrackingResponse repartidor
) {

    @Builder
    public record TrackingEstadoResponse(
            String estado,
            Instant fecha
    ) {
    }

    @Builder
    public record RepartidorTrackingResponse(
            String nombre,
            String telefono
    ) {
    }
}