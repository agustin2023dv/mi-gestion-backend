package com.migestion.analytics.application;

import com.migestion.analytics.dto.ProductoTopResponse;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetTopProductsUseCase {

    private static final int DEFAULT_LIMIT = 10;

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;

    public GetTopProductsUseCase(PedidoRepository pedidoRepository, PedidoItemRepository pedidoItemRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoTopResponse> execute(
            Long tenantId,
            String periodo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer limite) {
        AnalyticsTenantGuard.requireTenantAccess(tenantId);

        AnalyticsPeriodResolver.ResolvedPeriod resolvedPeriod = AnalyticsPeriodResolver.resolve(periodo, fechaInicio, fechaFin);
        List<Pedido> pedidos = pedidoRepository.findAllByTenantIdAndFechaPedidoGreaterThanEqualAndFechaPedidoLessThan(
                tenantId,
                resolvedPeriod.start(),
                resolvedPeriod.endExclusive());

        List<Long> pedidoIds = pedidos.stream().map(Pedido::getId).toList();
        if (pedidoIds.isEmpty()) {
            return List.of();
        }

        List<PedidoItem> items = pedidoItemRepository.findAllByPedidoIdIn(pedidoIds);
        Map<Long, ProductAccumulator> totalsByProduct = new HashMap<>();

        for (PedidoItem item : items) {
            ProductAccumulator accumulator = totalsByProduct.computeIfAbsent(
                    item.getProductoId(),
                    ignored -> new ProductAccumulator(item.getNombreProductoSnapshot()));

            accumulator.quantity += item.getCantidad() != null ? item.getCantidad() : 0;
            accumulator.revenue = accumulator.revenue.add(calculateItemRevenue(item));
            if ((accumulator.name == null || accumulator.name.isBlank()) && item.getNombreProductoSnapshot() != null) {
                accumulator.name = item.getNombreProductoSnapshot();
            }
        }

        int safeLimit = limite == null || limite <= 0 ? DEFAULT_LIMIT : limite;

        return totalsByProduct.entrySet().stream()
                .map(entry -> ProductoTopResponse.builder()
                        .productoId(entry.getKey())
                        .nombre(entry.getValue().name)
                        .cantidadVendida(entry.getValue().quantity)
                        .ingresosGenerados(entry.getValue().revenue)
                        .build())
                .sorted(Comparator.comparing(ProductoTopResponse::ingresosGenerados).reversed())
                .limit(safeLimit)
                .toList();
    }

    private BigDecimal calculateItemRevenue(PedidoItem item) {
        BigDecimal subtotal = item.getSubtotal();
        if (subtotal != null) {
            return subtotal;
        }
        return item.calcularSubtotal();
    }

    private static final class ProductAccumulator {
        private String name;
        private long quantity;
        private BigDecimal revenue = BigDecimal.ZERO;

        private ProductAccumulator(String name) {
            this.name = name;
        }
    }
}