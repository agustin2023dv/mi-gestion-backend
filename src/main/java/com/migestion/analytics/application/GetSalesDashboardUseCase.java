package com.migestion.analytics.application;

import com.migestion.analytics.dto.DashboardVentaResponse;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetSalesDashboardUseCase {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;

    public GetSalesDashboardUseCase(
            PedidoRepository pedidoRepository,
            PedidoItemRepository pedidoItemRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
    }

    @Transactional(readOnly = true)
    public DashboardVentaResponse execute(Long tenantId, String periodo, LocalDate fechaInicio, LocalDate fechaFin) {
        AnalyticsTenantGuard.requireTenantAccess(tenantId);

        AnalyticsPeriodResolver.ResolvedPeriod resolvedPeriod = AnalyticsPeriodResolver.resolve(periodo, fechaInicio, fechaFin);

        List<Pedido> pedidosPeriodo = pedidoRepository.findAllByTenantIdAndFechaPedidoGreaterThanEqualAndFechaPedidoLessThan(
                tenantId,
                resolvedPeriod.start(),
                resolvedPeriod.endExclusive());

        List<Pedido> pedidosPeriodoAnterior = pedidoRepository.findAllByTenantIdAndFechaPedidoGreaterThanEqualAndFechaPedidoLessThan(
                tenantId,
                resolvedPeriod.previousStart(),
                resolvedPeriod.previousEndExclusive());

        BigDecimal ingresosTotales = sumIngresos(pedidosPeriodo);
        long pedidosTotales = pedidosPeriodo.size();
        BigDecimal ticketPromedio = pedidosTotales > 0
                ? ingresosTotales.divide(BigDecimal.valueOf(pedidosTotales), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long productosVendidos = sumProductosVendidos(pedidosPeriodo);
        BigDecimal tasaCrecimiento = calculateGrowthRate(ingresosTotales, sumIngresos(pedidosPeriodoAnterior));

        return DashboardVentaResponse.builder()
                .periodo(resolvedPeriod.label())
                .metricas(DashboardVentaResponse.Metricas.builder()
                        .ingresosTotales(ingresosTotales)
                        .pedidosTotales(pedidosTotales)
                        .ticketPromedio(ticketPromedio)
                        .productosVendidos(productosVendidos)
                        .tasaCrecimiento(tasaCrecimiento)
                        .build())
                .build();
    }

    private BigDecimal sumIngresos(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(Pedido::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private long sumProductosVendidos(List<Pedido> pedidos) {
        List<Long> pedidoIds = pedidos.stream().map(Pedido::getId).toList();
        if (pedidoIds.isEmpty()) {
            return 0L;
        }

        List<PedidoItem> items = pedidoItemRepository.findAllByPedidoIdIn(pedidoIds);
        if (items == null) {
            items = Collections.emptyList();
        }

        return items.stream()
                .map(PedidoItem::getCantidad)
                .filter(cantidad -> cantidad != null)
                .mapToLong(Integer::longValue)
                .sum();
    }

    private BigDecimal calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? ONE_HUNDRED : BigDecimal.ZERO;
        }

        return current.subtract(previous)
                .multiply(ONE_HUNDRED)
                .divide(previous, 2, RoundingMode.HALF_UP);
    }
}