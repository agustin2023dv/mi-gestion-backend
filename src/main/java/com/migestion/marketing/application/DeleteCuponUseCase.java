package com.migestion.marketing.application;

import com.migestion.marketing.domain.Cupon;
import com.migestion.marketing.domain.CuponRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCuponUseCase {

    private final CuponRepository cuponRepository;

    public void execute(Long cuponId) {
        Long tenantId = requireTenantId();
        Cupon cupon = cuponRepository.findByIdAndTenantId(cuponId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon", cuponId));

        cupon.setDeletedAt(Instant.now());
        cuponRepository.save(cupon);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
