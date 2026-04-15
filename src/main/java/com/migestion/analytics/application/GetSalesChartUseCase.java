package com.migestion.analytics.application;

import com.migestion.analytics.dto.VentasGraficoResponse;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetSalesChartUseCase {

    private final PedidoRepository pedidoRepository;

    public GetSalesChartUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public VentasGraficoResponse execute(Long tenantId, String periodo, LocalDate fechaInicio, LocalDate fechaFin) {
        AnalyticsTenantGuard.requireTenantAccess(tenantId);

        AnalyticsPeriodResolver.ResolvedPeriod resolvedPeriod = AnalyticsPeriodResolver.resolve(periodo, fechaInicio, fechaFin);
        List<Pedido> pedidos = pedidoRepository.findAllByTenantIdAndFechaPedidoGreaterThanEqualAndFechaPedidoLessThan(
                tenantId,
                resolvedPeriod.start(),
                resolvedPeriod.endExclusive());

        Map<LocalDate, List<Pedido>> byDate = pedidos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        pedido -> pedido.getFechaPedido().atOffset(ZoneOffset.UTC).toLocalDate()));

        List<VentasGraficoResponse.Serie> series = byDate.entrySet().stream()
                .map(entry -> VentasGraficoResponse.Serie.builder()
                        .fecha(entry.getKey())
                        .ingresos(sumIngresos(entry.getValue()))
                        .pedidos((long) entry.getValue().size())
                        .build())
                .sorted(Comparator.comparing(VentasGraficoResponse.Serie::fecha))
                .toList();

        return VentasGraficoResponse.builder()
                .series(series)
                .build();
    }

    private BigDecimal sumIngresos(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(Pedido::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}