package com.migestion.finance.application;

import com.migestion.analytics.domain.SimuladorEscenario;
import com.migestion.analytics.domain.SimuladorEscenarioRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetEscenariosGuardadosUseCase {

    private final SimuladorEscenarioRepository simuladorEscenarioRepository;

    public GetEscenariosGuardadosUseCase(SimuladorEscenarioRepository simuladorEscenarioRepository) {
        this.simuladorEscenarioRepository = simuladorEscenarioRepository;
    }

    @Transactional(readOnly = true)
    public List<SimuladorEscenario> execute() {
        Long tenantId = requireTenantId();

        return simuladorEscenarioRepository.findAllByTenantId(tenantId).stream()
                .filter(SimuladorEscenario::isSaved)
                .toList();
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
