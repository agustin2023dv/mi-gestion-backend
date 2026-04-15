package com.migestion.finance.application;

import com.migestion.finance.domain.AsignacionCostoIndirecto;
import com.migestion.finance.domain.ProrrateoService;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalcularProrrateoUseCase {

    private final ProrrateoService prorrateoService;

    public CalcularProrrateoUseCase(ProrrateoService prorrateoService) {
        this.prorrateoService = prorrateoService;
    }

    @Transactional(readOnly = true)
    public List<AsignacionCostoIndirecto> execute(Instant periodoInicio, Instant periodoFin, Long criterioId) {
        Long tenantId = requireTenantId();
        return prorrateoService.calcularProrrateo(tenantId, periodoInicio, periodoFin, criterioId);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
