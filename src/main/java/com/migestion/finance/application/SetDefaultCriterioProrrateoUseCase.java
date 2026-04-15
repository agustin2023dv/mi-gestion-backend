package com.migestion.finance.application;

import com.migestion.finance.domain.CriterioProrrateo;
import com.migestion.finance.domain.CriterioProrrateoRepository;
import com.migestion.finance.dto.CriterioProrrateoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetDefaultCriterioProrrateoUseCase {

    private final CriterioProrrateoRepository criterioProrrateoRepository;
    private final FinanceMapper financeMapper;

    public SetDefaultCriterioProrrateoUseCase(
            CriterioProrrateoRepository criterioProrrateoRepository,
            FinanceMapper financeMapper
    ) {
        this.criterioProrrateoRepository = criterioProrrateoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public CriterioProrrateoResponse execute(Long criterioId) {
        Long tenantId = requireTenantId();

        CriterioProrrateo criterioObjetivo = criterioProrrateoRepository.findByIdAndTenantId(criterioId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CriterioProrrateo", criterioId));

        List<CriterioProrrateo> defaultCriteria = criterioProrrateoRepository.findAllByTenantId(tenantId)
                .stream()
                .filter(CriterioProrrateo::isDefault)
                .filter(criterio -> !criterio.getId().equals(criterioObjetivo.getId()))
                .toList();

        if (!defaultCriteria.isEmpty()) {
            defaultCriteria.forEach(criterio -> criterio.setDefault(false));
            criterioProrrateoRepository.saveAll(defaultCriteria);
        }

        criterioObjetivo.setDefault(true);
        CriterioProrrateo persistedCriterio = criterioProrrateoRepository.save(criterioObjetivo);
        return financeMapper.toCriterioProrrateoResponse(persistedCriterio);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}