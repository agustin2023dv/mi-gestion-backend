package com.migestion.finance.application;

import com.migestion.finance.domain.CentroCostoRepository;
import com.migestion.finance.dto.CentroCostoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllCentrosCostoUseCase {

    private final CentroCostoRepository centroCostoRepository;
    private final FinanceMapper financeMapper;

    public GetAllCentrosCostoUseCase(
            CentroCostoRepository centroCostoRepository,
            FinanceMapper financeMapper
    ) {
        this.centroCostoRepository = centroCostoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional(readOnly = true)
    public List<CentroCostoResponse> execute() {
        Long tenantId = requireTenantId();

        return centroCostoRepository.findAllByTenantId(tenantId)
                .stream()
                .map(financeMapper::toCentroCostoResponse)
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