package com.migestion.finance.application;

import com.migestion.analytics.domain.SimuladorEscenario;
import com.migestion.analytics.domain.SimuladorEscenarioRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteEscenarioUseCase {

    private final SimuladorEscenarioRepository simuladorEscenarioRepository;

    public DeleteEscenarioUseCase(SimuladorEscenarioRepository simuladorEscenarioRepository) {
        this.simuladorEscenarioRepository = simuladorEscenarioRepository;
    }

    @Transactional
    public void execute(Long escenarioId) {
        Long tenantId = requireTenantId();

        SimuladorEscenario escenario = simuladorEscenarioRepository.findByIdAndTenantId(escenarioId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("SimuladorEscenario", escenarioId));

        simuladorEscenarioRepository.delete(escenario);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
