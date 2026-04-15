package com.migestion.finance.application;

import com.migestion.finance.domain.CriterioProrrateo;
import com.migestion.finance.domain.CriterioProrrateoRepository;
import com.migestion.finance.dto.CriterioProrrateoRequest;
import com.migestion.finance.dto.CriterioProrrateoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCriterioProrrateoUseCase {

    private final CriterioProrrateoRepository criterioProrrateoRepository;
    private final FinanceMapper financeMapper;

    public UpdateCriterioProrrateoUseCase(
            CriterioProrrateoRepository criterioProrrateoRepository,
            FinanceMapper financeMapper
    ) {
        this.criterioProrrateoRepository = criterioProrrateoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public CriterioProrrateoResponse execute(Long criterioId, CriterioProrrateoRequest request) {
        Long tenantId = requireTenantId();

        CriterioProrrateo criterioProrrateo = criterioProrrateoRepository.findByIdAndTenantId(criterioId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CriterioProrrateo", criterioId));

        if (request.nombre() != null) {
            criterioProrrateo.setNombre(request.nombre());
        }
        if (request.tipo() != null) {
            criterioProrrateo.setTipo(request.tipo());
        }
        if (request.formula() != null) {
            criterioProrrateo.setFormula(request.formula());
        }
        if (request.parametrosJson() != null) {
            criterioProrrateo.setParametrosJson(request.parametrosJson());
        }
        if (request.descripcion() != null) {
            criterioProrrateo.setDescripcion(request.descripcion());
        }
        if (request.isDefault() != null) {
            if (request.isDefault()) {
                clearDefaultCriteria(tenantId, criterioProrrateo.getId());
            }
            criterioProrrateo.setDefault(request.isDefault());
        }

        CriterioProrrateo persistedCriterio = criterioProrrateoRepository.save(criterioProrrateo);
        return financeMapper.toCriterioProrrateoResponse(persistedCriterio);
    }

    private void clearDefaultCriteria(Long tenantId, Long excludedCriterioId) {
        List<CriterioProrrateo> defaultCriteria = criterioProrrateoRepository.findAllByTenantId(tenantId)
                .stream()
                .filter(CriterioProrrateo::isDefault)
                .filter(criterio -> !criterio.getId().equals(excludedCriterioId))
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