package com.migestion.analytics.application;

import com.migestion.analytics.domain.DashboardCosto;
import com.migestion.analytics.domain.DashboardCostoRepository;
import com.migestion.analytics.dto.DashboardCostoResponse;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCostDashboardUseCase {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final DashboardCostoRepository dashboardCostoRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;

    public GetCostDashboardUseCase(
            DashboardCostoRepository dashboardCostoRepository,
            PedidoRepository pedidoRepository,
            PedidoItemRepository pedidoItemRepository) {
        this.dashboardCostoRepository = dashboardCostoRepository;
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
    }

    @Transactional(readOnly = true)
    public DashboardCostoResponse execute(Long tenantId, String periodo, LocalDate fechaInicio, LocalDate fechaFin) {
        AnalyticsTenantGuard.requireTenantAccess(tenantId);

        AnalyticsPeriodResolver.ResolvedPeriod resolvedPeriod = AnalyticsPeriodResolver.resolve(periodo, fechaInicio, fechaFin);
        List<DashboardCosto> snapshots = dashboardCostoRepository.findAllByTenantIdAndFechaCalculoGreaterThanEqualAndFechaCalculoLessThan(
                tenantId,
                resolvedPeriod.start(),
                resolvedPeriod.endExclusive());

        List<Pedido> pedidos = pedidoRepository.findAllByTenantIdAndFechaPedidoGreaterThanEqualAndFechaPedidoLessThan(
                tenantId,
                resolvedPeriod.start(),
                resolvedPeriod.endExclusive());

        BigDecimal ingresosTotales = sumPedidosTotal(pedidos);
        CostComponents components = snapshots.isEmpty()
                ? calculateOnTheFlyCosts(pedidos)
                : aggregateSnapshotCosts(snapshots);

        BigDecimal costoTotal = components.costoIngredientes
                .add(components.costoEmpleados)
                .add(components.costoCifAsignado);
        BigDecimal margenBruto = ingresosTotales.subtract(costoTotal);
        BigDecimal margenPorcentual = ingresosTotales.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : margenBruto.multiply(ONE_HUNDRED).divide(ingresosTotales, 2, RoundingMode.HALF_UP);
        BigDecimal margenNeto = margenBruto.subtract(components.gastosFijosTotales);

        return DashboardCostoResponse.builder()
                .periodo(resolvedPeriod.label())
                .metricas(DashboardCostoResponse.Metricas.builder()
                        .ingresosTotales(ingresosTotales)
                        .costoIngredientes(components.costoIngredientes)
                        .costoEmpleados(components.costoEmpleados)
                        .costoCIFAsignado(components.costoCifAsignado)
                        .costoTotal(costoTotal)
                        .margenBruto(margenBruto)
                        .margenPorcentual(margenPorcentual)
                        .gastosFijosTotales(components.gastosFijosTotales)
                        .margenNeto(margenNeto)
                        .bepUnidades(components.bepUnidades)
                        .bepMonto(components.bepMonto)
                        .build())
                .build();
    }

    private CostComponents calculateOnTheFlyCosts(List<Pedido> pedidos) {
        List<Long> pedidoIds = pedidos.stream().map(Pedido::getId).toList();
        if (pedidoIds.isEmpty()) {
            return CostComponents.empty();
        }

        List<PedidoItem> items = pedidoItemRepository.findAllByPedidoIdIn(pedidoIds);
        BigDecimal ingredientCost = items.stream()
                .map(this::calculateItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CostComponents(
                ingredientCost,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    private CostComponents aggregateSnapshotCosts(List<DashboardCosto> snapshots) {
        BigDecimal costoIngredientes = snapshots.stream()
                .map(DashboardCosto::getCostosIngredientesPeriodo)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal costoEmpleados = snapshots.stream()
                .map(DashboardCosto::getCostosEmpleadosPeriodo)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal costoCifAsignado = snapshots.stream()
                .map(DashboardCosto::getCogsCifAsignado)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gastosFijosTotales = snapshots.stream()
                .map(DashboardCosto::getGastosFijosTotales)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bepUnidades = snapshots.stream()
                .map(DashboardCosto::getBepUnidades)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bepMonto = snapshots.stream()
                .map(DashboardCosto::getBepMonto)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CostComponents(
                costoIngredientes,
                costoEmpleados,
                costoCifAsignado,
                gastosFijosTotales,
                bepUnidades,
                bepMonto);
    }

    private BigDecimal calculateItemCost(PedidoItem item) {
        BigDecimal unitCost = item.getCostoUnitarioSnapshot() != null ? item.getCostoUnitarioSnapshot() : BigDecimal.ZERO;
        int quantity = item.getCantidad() != null ? item.getCantidad() : 0;
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }

    private BigDecimal sumPedidosTotal(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(Pedido::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private record CostComponents(
            BigDecimal costoIngredientes,
            BigDecimal costoEmpleados,
            BigDecimal costoCifAsignado,
            BigDecimal gastosFijosTotales,
            BigDecimal bepUnidades,
            BigDecimal bepMonto
    ) {
        private static CostComponents empty() {
            return new CostComponents(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO);
        }
    }
}