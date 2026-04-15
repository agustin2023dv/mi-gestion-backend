package com.migestion.finance.application;

import com.migestion.analytics.domain.SimuladorEscenario;
import com.migestion.analytics.domain.SimuladorEscenarioRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuardarEscenarioUseCase {

    private final SimuladorEscenarioRepository simuladorEscenarioRepository;
    private final SimularEscenarioUseCase simularEscenarioUseCase;

    public GuardarEscenarioUseCase(
            SimuladorEscenarioRepository simuladorEscenarioRepository,
            SimularEscenarioUseCase simularEscenarioUseCase
    ) {
        this.simuladorEscenarioRepository = simuladorEscenarioRepository;
        this.simularEscenarioUseCase = simularEscenarioUseCase;
    }

    @Transactional
    public SimuladorEscenario execute(
            String nombreEscenario,
            BigDecimal precioPromedio,
            BigDecimal costoVariable,
            BigDecimal costosEmpleados,
            BigDecimal costosIngredientes,
            BigDecimal gastosFijos
    ) {
        Long tenantId = requireTenantId();

        String nombreNormalizado = normalizeNombreEscenario(nombreEscenario);
        SimularEscenarioUseCase.ResultadoSimulacion simulacion = simularEscenarioUseCase.execute(
                precioPromedio,
                costoVariable,
                costosEmpleados,
                costosIngredientes,
                gastosFijos
        );

        SimuladorEscenario escenario = SimuladorEscenario.builder()
                .tenantId(tenantId)
                .nombreEscenario(nombreNormalizado)
                .precioPromedioSimulado(simulacion.precioPromedioSimulado())
                .costoVariableSimulado(simulacion.costoVariableSimulado())
                .costosEmpleadosSimulado(simulacion.costosEmpleadosSimulado())
                .costosIngredientesSimulado(simulacion.costosIngredientesSimulado())
                .gastosFijosSimulado(simulacion.gastosFijosSimulado())
                .margenSimulado(simulacion.margenSimulado())
                .bepSimulado(simulacion.bepSimulado())
                .isSaved(true)
                .build();

        return simuladorEscenarioRepository.save(escenario);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }

    private String normalizeNombreEscenario(String nombreEscenario) {
        if (nombreEscenario == null || nombreEscenario.isBlank()) {
            throw new BusinessRuleViolationException("NOMBRE_ESCENARIO_REQUERIDO", "nombreEscenario is required");
        }

        String normalized = nombreEscenario.trim();
        if (normalized.length() > 100) {
            throw new BusinessRuleViolationException("NOMBRE_ESCENARIO_INVALIDO", "nombreEscenario cannot exceed 100 characters");
        }

        return normalized;
    }
}
