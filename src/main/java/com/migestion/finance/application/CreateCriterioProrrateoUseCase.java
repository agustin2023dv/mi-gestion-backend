package com.migestion.finance.application;

import com.migestion.finance.domain.CriterioProrrateo;
import com.migestion.finance.domain.CriterioProrrateoRepository;
import com.migestion.finance.dto.CriterioProrrateoRequest;
import com.migestion.finance.dto.CriterioProrrateoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCriterioProrrateoUseCase {

    private final CriterioProrrateoRepository criterioProrrateoRepository;
    private final FinanceMapper financeMapper;

    public CreateCriterioProrrateoUseCase(
            CriterioProrrateoRepository criterioProrrateoRepository,
            FinanceMapper financeMapper
    ) {
        this.criterioProrrateoRepository = criterioProrrateoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public CriterioProrrateoResponse execute(CriterioProrrateoRequest request) {
        Long tenantId = requireTenantId();

        CriterioProrrateo criterioProrrateo = financeMapper.toCriterioProrrateo(request);
        criterioProrrateo.setTenantId(tenantId);
        criterioProrrateo.setDefault(Boolean.TRUE.equals(request.isDefault()));

        if (criterioProrrateo.isDefault()) {
            clearDefaultCriteria(tenantId, null);
        }

        CriterioProrrateo persistedCriterio = criterioProrrateoRepository.save(criterioProrrateo);
        return financeMapper.toCriterioProrrateoResponse(persistedCriterio);
    }

    private void clearDefaultCriteria(Long tenantId, Long excludedCriterioId) {
        List<CriterioProrrateo> defaultCriteria = criterioProrrateoRepository.findAllByTenantId(tenantId)
                .stream()
                .filter(CriterioProrrateo::isDefault)
                .filter(criterio -> excludedCriterioId == null || !criterio.getId().equals(excludedCriterioId))
                .toList();

        if (defaultCriteria.isEmpty()) {
            return;
        }

        defaultCriteria.forEach(criterio -> criterio.setDefault(false));
        criterioProrrateoRepository.saveAll(defaultCriteria);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}