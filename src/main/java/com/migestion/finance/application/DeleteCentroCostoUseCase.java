package com.migestion.finance.application;

import com.migestion.finance.domain.CentroCosto;
import com.migestion.finance.domain.CentroCostoRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCentroCostoUseCase {

    private final CentroCostoRepository centroCostoRepository;

    public DeleteCentroCostoUseCase(CentroCostoRepository centroCostoRepository) {
        this.centroCostoRepository = centroCostoRepository;
    }

    @Transactional
    public void execute(Long centroCostoId) {
        Long tenantId = requireTenantId();

        CentroCosto centroCosto = centroCostoRepository.findByIdAndTenantId(centroCostoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CentroCosto", centroCostoId));

        centroCosto.setDeletedAt(Instant.now());
        centroCostoRepository.save(centroCosto);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}