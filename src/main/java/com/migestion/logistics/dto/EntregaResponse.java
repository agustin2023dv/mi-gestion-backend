package com.migestion.logistics.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record EntregaResponse(
        Long id,
        Long tenantId,
        String estado,
        BigDecimal latitudActual,
        BigDecimal longitudActual,
        BigDecimal distanciaVerificada,
        boolean geolocalizacionValidada,
        boolean verificacionEdadHecha,
        Instant asignadoEn,
        Instant inicioEntrega,
        Instant entregaConfirmada,
        Instant createdAt,
        Instant updatedAt,
        PedidoInfo pedido,
        RepartidorInfo repartidor
) {

    @Builder
    public record PedidoInfo(
            Long id,
            String numeroPedido,
            String tipoEntrega,
            DireccionInfo direccionEntrega,
            ClienteInfo cliente,
            BigDecimal total,
            String metodoPago,
            boolean requiereVerificacionEdad
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

    @Builder
    public record DireccionInfo(
            String calle,
            String numero,
            String ciudad,
            String provincia,
            String codigoPostal,
            BigDecimal latitud,
            BigDecimal longitud
    ) {
    }

    @Builder
    public record ClienteInfo(
            String nombre,
            String apellido,
            String telefono
    ) {
    }
}
