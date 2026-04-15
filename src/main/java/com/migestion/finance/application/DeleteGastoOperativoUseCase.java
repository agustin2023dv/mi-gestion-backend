package com.migestion.finance.application;

import com.migestion.finance.domain.GastoOperativoRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteGastoOperativoUseCase {

    private final GastoOperativoRepository gastoOperativoRepository;

    public DeleteGastoOperativoUseCase(GastoOperativoRepository gastoOperativoRepository) {
        this.gastoOperativoRepository = gastoOperativoRepository;
    }

    @Transactional
    public void execute(Long gastoOperativoId) {
        Long tenantId = requireTenantId();

        gastoOperativoRepository.findByIdAndTenantId(gastoOperativoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("GastoOperativo", gastoOperativoId));

        gastoOperativoRepository.deleteById(gastoOperativoId);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
