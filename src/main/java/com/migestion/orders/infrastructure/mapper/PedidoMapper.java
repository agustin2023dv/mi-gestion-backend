package com.migestion.orders.infrastructure.mapper;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoEstadoHistorial;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.dto.PedidoResponse;
import com.migestion.orders.dto.PedidoTrackingResponse;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PedidoMapper {

    @Mapping(target = "estado", source = "pedido.estado.codigo")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "updatedAt", source = "pedido.updatedAt")
    @Mapping(target = "createdAt", source = "pedido.createdAt")
    PedidoResponse toResponse(Pedido pedido, List<PedidoItem> items);

    @Mapping(target = "nombreProducto", source = "nombreProductoSnapshot")
    @Mapping(target = "precioUnitario", source = "precioBaseSnapshot")
    @Mapping(target = "precioExtras", source = "precioExtrasSnapshot")
    PedidoResponse.ItemPedidoResponse toItemResponse(PedidoItem item);

    @Mapping(target = "estadoActual", source = "pedido.estado.codigo")
    @Mapping(target = "historial", source = "historial")
    @Mapping(target = "repartidor", ignore = true)
    PedidoTrackingResponse toTrackingResponse(Pedido pedido, List<PedidoEstadoHistorial> historial);

    @Mapping(target = "estado", source = "estado.codigo")
    @Mapping(target = "fecha", source = "fechaCambio")
    PedidoTrackingResponse.TrackingEstadoResponse toTrackingEstadoResponse(PedidoEstadoHistorial historial);
}