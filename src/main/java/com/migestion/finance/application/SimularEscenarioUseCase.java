package com.migestion.finance.application;

import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Builder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimularEscenarioUseCase {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Transactional(readOnly = true)
    public ResultadoSimulacion execute(
            BigDecimal precioPromedio,
            BigDecimal costoVariable,
            BigDecimal costosEmpleados,
            BigDecimal costosIngredientes,
            BigDecimal gastosFijos
    ) {
        requireTenantId();

        BigDecimal precioPromedioNormalizado = normalizeNonNegative(precioPromedio, "precioPromedio");
        BigDecimal costoVariableNormalizado = normalizeNonNegative(costoVariable, "costoVariable");
        BigDecimal costosEmpleadosNormalizados = normalizeNonNegative(costosEmpleados, "costosEmpleados");
        BigDecimal costosIngredientesNormalizados = normalizeNonNegative(costosIngredientes, "costosIngredientes");
        BigDecimal gastosFijosNormalizados = normalizeNonNegative(gastosFijos, "gastosFijos");

        BigDecimal costoVariableTotal = costoVariableNormalizado
                .add(costosEmpleadosNormalizados)
                .add(costosIngredientesNormalizados);

        BigDecimal margenContribucion = precioPromedioNormalizado.subtract(costoVariableTotal);

        BigDecimal margenSimulado = precioPromedioNormalizado.compareTo(BigDecimal.ZERO) <= 0
                ? BigDecimal.ZERO
                : margenContribucion.multiply(ONE_HUNDRED).divide(precioPromedioNormalizado, 2, RoundingMode.HALF_UP);

        BigDecimal bepSimulado = margenContribucion.compareTo(BigDecimal.ZERO) <= 0
                ? BigDecimal.ZERO
                : gastosFijosNormalizados.divide(margenContribucion, 2, RoundingMode.HALF_UP);

        BigDecimal ingresosNecesarios = bepSimulado.multiply(precioPromedioNormalizado)
                .setScale(2, RoundingMode.HALF_UP);

        return ResultadoSimulacion.builder()
                .precioPromedioSimulado(precioPromedioNormalizado)
                .costoVariableSimulado(costoVariableNormalizado)
                .costosEmpleadosSimulado(costosEmpleadosNormalizados)
                .costosIngredientesSimulado(costosIngredientesNormalizados)
                .gastosFijosSimulado(gastosFijosNormalizados)
                .margenSimulado(margenSimulado)
                .bepSimulado(bepSimulado)
                .ingresosNecesarios(ingresosNecesarios)
                .build();
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }

    private BigDecimal normalizeNonNegative(BigDecimal value, String fieldName) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException(
                    "SIMULACION_VALOR_INVALIDO",
                    fieldName + " cannot be negative"
            );
        }

        return value;
    }

    @Builder
    public record ResultadoSimulacion(
            BigDecimal precioPromedioSimulado,
            BigDecimal costoVariableSimulado,
            BigDecimal costosEmpleadosSimulado,
            BigDecimal costosIngredientesSimulado,
            BigDecimal gastosFijosSimulado,
            BigDecimal margenSimulado,
            BigDecimal bepSimulado,
            BigDecimal ingresosNecesarios
    ) {
    }
}
