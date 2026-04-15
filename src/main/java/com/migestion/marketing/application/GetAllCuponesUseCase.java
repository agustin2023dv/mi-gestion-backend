package com.migestion.marketing.application;

import com.migestion.marketing.domain.Cupon;
import com.migestion.marketing.domain.CuponRepository;
import com.migestion.marketing.dto.CuponResponse;
import com.migestion.marketing.infrastructure.mapper.CuponMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllCuponesUseCase {

    private final CuponRepository cuponRepository;
    private final CuponMapper cuponMapper;

    public List<CuponResponse> execute(Boolean isActive, Pageable pageable) {
        Long tenantId = requireTenantId();

        Page<Cupon> cuponesPage = isActive == null
                ? cuponRepository.findAllByTenantId(tenantId, pageable)
                : cuponRepository.findAllByTenantIdAndIsActive(tenantId, isActive, pageable);

        return cuponesPage.getContent().stream().map(cuponMapper::toResponse).toList();
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
