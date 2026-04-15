package com.migestion.finance.application;

import com.migestion.finance.domain.CriterioProrrateoRepository;
import com.migestion.finance.dto.CriterioProrrateoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllCriteriosProrrateoUseCase {

    private final CriterioProrrateoRepository criterioProrrateoRepository;
    private final FinanceMapper financeMapper;

    public GetAllCriteriosProrrateoUseCase(
            CriterioProrrateoRepository criterioProrrateoRepository,
            FinanceMapper financeMapper
    ) {
        this.criterioProrrateoRepository = criterioProrrateoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional(readOnly = true)
    public List<CriterioProrrateoResponse> execute() {
        Long tenantId = requireTenantId();

        return criterioProrrateoRepository.findAllByTenantId(tenantId)
                .stream()
                .map(financeMapper::toCriterioProrrateoResponse)
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