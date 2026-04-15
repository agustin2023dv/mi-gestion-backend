package com.migestion.marketing.application;

import com.migestion.marketing.domain.Cupon;
import com.migestion.marketing.domain.CuponRepository;
import com.migestion.marketing.dto.CuponResponse;
import com.migestion.marketing.infrastructure.mapper.CuponMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCuponByIdUseCase {

    private final CuponRepository cuponRepository;
    private final CuponMapper cuponMapper;

    public CuponResponse execute(Long cuponId) {
        Long tenantId = requireTenantId();
        Cupon cupon = cuponRepository.findByIdAndTenantId(cuponId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon", cuponId));
        return cuponMapper.toResponse(cupon);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
