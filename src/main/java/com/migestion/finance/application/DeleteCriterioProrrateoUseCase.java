package com.migestion.finance.application;

import com.migestion.finance.domain.CriterioProrrateo;
import com.migestion.finance.domain.CriterioProrrateoRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCriterioProrrateoUseCase {

    private final CriterioProrrateoRepository criterioProrrateoRepository;

    public DeleteCriterioProrrateoUseCase(CriterioProrrateoRepository criterioProrrateoRepository) {
        this.criterioProrrateoRepository = criterioProrrateoRepository;
    }

    @Transactional
    public void execute(Long id) {
        Long tenantId = requireTenantId();

        CriterioProrrateo criterio = criterioProrrateoRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CriterioProrrateo", id));

        if (criterio.isDefault()) {
            List<CriterioProrrateo> allCriteria = criterioProrrateoRepository.findAllByTenantId(tenantId);
            long otherCount = allCriteria.stream()
                    .filter(c -> !c.getId().equals(id))
                    .count();
            if (otherCount > 0) {
                throw new BusinessRuleViolationException(
                        "DEFAULT_CRITERIO_CANNOT_BE_DELETED",
                        "Cannot delete the default proration criterion while other criteria exist"
                );
            }
        }

        criterio.setDeletedAt(Instant.now());
        criterioProrrateoRepository.save(criterio);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
