package com.migestion.orders.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record PedidoResponse(
        Long id,
        Long tenantId,
        Long clienteId,
        Long repartidorId,
        Long direccionEntregaId,
        String numeroPedido,
        String estado,
        String estadoPago,
        String tipoEntrega,
        Long tarifaDeliveryId,
        List<ItemPedidoResponse> items,
        BigDecimal subtotal,
        BigDecimal impuestos,
        BigDecimal costoEnvio,
        BigDecimal descuentoInicial,
        BigDecimal descuentoAdicional,
        BigDecimal total,
        String metodoPago,
        String trackingToken,
        Instant fechaPedido,
        Instant fechaEntregaSolicitada,
        String notasCliente,
        String motivoCancelacion,
        Instant fechaCancelacion,
        Instant createdAt,
        Instant updatedAt
) {

    @Builder
    public record ItemPedidoResponse(
            Long id,
            Long productoId,
            Integer cantidad,
            String nombreProducto,
            BigDecimal precioUnitario,
            BigDecimal precioExtras,
            BigDecimal subtotal
    ) {
    }
}