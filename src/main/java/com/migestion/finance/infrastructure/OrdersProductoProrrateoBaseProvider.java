package com.migestion.finance.infrastructure;

import com.migestion.finance.domain.ProductoBaseProrrateo;
import com.migestion.finance.domain.ProductoProrrateoBaseProvider;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OrdersProductoProrrateoBaseProvider implements ProductoProrrateoBaseProvider {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;

    public OrdersProductoProrrateoBaseProvider(
            PedidoRepository pedidoRepository,
            PedidoItemRepository pedidoItemRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
    }

    @Override
    public List<ProductoBaseProrrateo> getBasePorProducto(Long tenantId, Instant periodoInicio, Instant periodoFin) {
        List<Pedido> pedidos = pedidoRepository.findAllByTenantIdAndFechaPedidoGreaterThanEqualAndFechaPedidoLessThan(
                tenantId,
                periodoInicio,
                periodoFin
        );

        if (pedidos.isEmpty()) {
            return List.of();
        }

        List<Long> pedidoIds = pedidos.stream().map(Pedido::getId).toList();
        List<PedidoItem> items = pedidoItemRepository.findAllByPedidoIdIn(pedidoIds);

        Map<Long, ProductoBaseAccumulator> acumulados = new HashMap<>();
        for (PedidoItem item : items) {
            ProductoBaseAccumulator accumulator = acumulados.computeIfAbsent(
                    item.getProductoId(),
                    ignored -> new ProductoBaseAccumulator()
            );

            int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
            accumulator.unidades = accumulator.unidades.add(BigDecimal.valueOf(cantidad));
            accumulator.ingresos = accumulator.ingresos.add(resolveSubtotal(item));
        }

        return acumulados.entrySet().stream()
                .map(entry -> ProductoBaseProrrateo.builder()
                        .productoId(entry.getKey())
                        .unidadesVendidas(entry.getValue().unidades)
                        .ingresosGenerados(entry.getValue().ingresos)
                        .build())
                .toList();
    }

    private BigDecimal resolveSubtotal(PedidoItem item) {
        if (item.getSubtotal() != null) {
            return item.getSubtotal();
        }
        return item.calcularSubtotal();
    }

    private static final class ProductoBaseAccumulator {
        private BigDecimal unidades = BigDecimal.ZERO;
        private BigDecimal ingresos = BigDecimal.ZERO;
    }
}
