package com.migestion.finance.application;

import com.migestion.finance.domain.GastoOperativo;
import com.migestion.finance.domain.GastoOperativoRepository;
import com.migestion.finance.dto.GastoOperativoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetGastoOperativoByIdUseCase {

    private final GastoOperativoRepository gastoOperativoRepository;
    private final FinanceMapper financeMapper;

    public GetGastoOperativoByIdUseCase(
            GastoOperativoRepository gastoOperativoRepository,
            FinanceMapper financeMapper
    ) {
        this.gastoOperativoRepository = gastoOperativoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional(readOnly = true)
    public GastoOperativoResponse execute(Long gastoOperativoId) {
        Long tenantId = requireTenantId();

        GastoOperativo gastoOperativo = gastoOperativoRepository.findByIdAndTenantId(gastoOperativoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("GastoOperativo", gastoOperativoId));

        return financeMapper.toGastoOperativoResponse(gastoOperativo);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
