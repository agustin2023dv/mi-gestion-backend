package com.migestion.finance.application;

import com.migestion.finance.domain.AsignacionCostoIndirecto;
import com.migestion.finance.domain.CriterioProrrateo;
import com.migestion.finance.domain.CriterioProrrateoRepository;
import com.migestion.finance.domain.GastoOperativo;
import com.migestion.finance.domain.GastoOperativoRepository;
import com.migestion.finance.domain.ProductoBaseProrrateo;
import com.migestion.finance.domain.ProductoProrrateoBaseProvider;
import com.migestion.finance.domain.ProrrateoService;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class ProrrateoServiceImpl implements ProrrateoService {

    private static final String BASE_UNIDADES = "UNIDADES_VENDIDAS";
    private static final String BASE_INGRESOS = "INGRESOS";

    private final GastoOperativoRepository gastoOperativoRepository;
    private final CriterioProrrateoRepository criterioProrrateoRepository;
    private final ProductoProrrateoBaseProvider productoProrrateoBaseProvider;

    public ProrrateoServiceImpl(
            GastoOperativoRepository gastoOperativoRepository,
            CriterioProrrateoRepository criterioProrrateoRepository,
            ProductoProrrateoBaseProvider productoProrrateoBaseProvider
    ) {
        this.gastoOperativoRepository = gastoOperativoRepository;
        this.criterioProrrateoRepository = criterioProrrateoRepository;
        this.productoProrrateoBaseProvider = productoProrrateoBaseProvider;
    }

    @Override
    public List<AsignacionCostoIndirecto> calcularProrrateo(
            Long tenantId,
            Instant periodoInicio,
            Instant periodoFin,
            Long criterioId
    ) {
        validatePeriod(periodoInicio, periodoFin);

        CriterioProrrateo criterio = criterioProrrateoRepository.findByIdAndTenantId(criterioId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CriterioProrrateo", criterioId));

        List<GastoOperativo> gastosProrrateables = gastoOperativoRepository.findProrrateableGastos(
                tenantId,
                periodoInicio,
                periodoFin
        );

        if (gastosProrrateables.isEmpty()) {
            return List.of();
        }

        List<ProductoBaseProrrateo> basePorProducto = productoProrrateoBaseProvider.getBasePorProducto(
                tenantId,
                periodoInicio,
                periodoFin
        );

        if (basePorProducto.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "PRORRATEO_BASE_NOT_FOUND",
                    "No products with sales were found for the selected period"
            );
        }

        Function<ProductoBaseProrrateo, BigDecimal> baseSelector = resolveBaseSelector(criterio.getTipo());
        BigDecimal baseTotal = basePorProducto.stream()
                .map(baseSelector)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (baseTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException(
                    "PRORRATEO_INVALID_BASE",
                    "Proration base total must be greater than zero"
            );
        }

        Instant fechaCalculo = Instant.now();
        List<AsignacionCostoIndirecto> asignaciones = new ArrayList<>();

        for (GastoOperativo gasto : gastosProrrateables) {
            if (gasto.getCentroCostoId() == null) {
                throw new BusinessRuleViolationException(
                        "PRORRATEO_CENTRO_COSTO_REQUIRED",
                        "All proratable expenses must have a centroCostoId"
                );
            }

            asignaciones.addAll(distribuirGasto(
                    tenantId,
                    criterio,
                    gasto,
                    basePorProducto,
                    baseSelector,
                    baseTotal,
                    periodoInicio,
                    periodoFin,
                    fechaCalculo
            ));
        }

        return asignaciones;
    }

    private List<AsignacionCostoIndirecto> distribuirGasto(
            Long tenantId,
            CriterioProrrateo criterio,
            GastoOperativo gasto,
            List<ProductoBaseProrrateo> basePorProducto,
            Function<ProductoBaseProrrateo, BigDecimal> baseSelector,
            BigDecimal baseTotal,
            Instant periodoInicio,
            Instant periodoFin,
            Instant fechaCalculo
    ) {
        List<AsignacionCostoIndirecto> asignaciones = new ArrayList<>();
        BigDecimal asignadoAcumulado = BigDecimal.ZERO;

        for (int i = 0; i < basePorProducto.size(); i++) {
            ProductoBaseProrrateo base = basePorProducto.get(i);
            BigDecimal montoAsignado;

            if (i == basePorProducto.size() - 1) {
                montoAsignado = gasto.getMonto().subtract(asignadoAcumulado);
            } else {
                BigDecimal factor = baseSelector.apply(base)
                        .divide(baseTotal, 12, RoundingMode.HALF_UP);
                montoAsignado = gasto.getMonto()
                        .multiply(factor)
                        .setScale(2, RoundingMode.HALF_UP);
                asignadoAcumulado = asignadoAcumulado.add(montoAsignado);
            }

            asignaciones.add(AsignacionCostoIndirecto.builder()
                    .tenantId(tenantId)
                    .gastoOperativoId(gasto.getId())
                    .criterioProrrateoId(criterio.getId())
                    .productoId(base.productoId())
                    .centroCostoId(gasto.getCentroCostoId())
                    .montoAsignado(montoAsignado)
                    .periodoInicio(periodoInicio)
                    .periodoFin(periodoFin)
                    .fechaCalculo(fechaCalculo)
                    .notas("Prorrateo por " + criterio.getTipo())
                    .build());
        }

        return asignaciones;
    }

    private Function<ProductoBaseProrrateo, BigDecimal> resolveBaseSelector(String criterioTipo) {
        String normalized = criterioTipo == null
                ? ""
                : criterioTipo.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');

        if (normalized.contains(BASE_UNIDADES)) {
            return base -> nullSafe(base.unidadesVendidas());
        }
        if (normalized.contains(BASE_INGRESOS)
                || normalized.contains("VENTAS")
                || normalized.contains("REVENUE")) {
            return base -> nullSafe(base.ingresosGenerados());
        }

        throw new BusinessRuleViolationException(
                "PRORRATEO_CRITERIO_TIPO_UNSUPPORTED",
                "Unsupported proration criterion type: " + criterioTipo
        );
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void validatePeriod(Instant periodoInicio, Instant periodoFin) {
        if (periodoInicio == null || periodoFin == null) {
            throw new BusinessRuleViolationException(
                    "PRORRATEO_PERIOD_REQUIRED",
                    "periodoInicio and periodoFin are required"
            );
        }

        if (!periodoInicio.isBefore(periodoFin)) {
            throw new BusinessRuleViolationException(
                    "PRORRATEO_PERIOD_INVALID",
                    "periodoInicio must be before periodoFin"
            );
        }
    }
}
