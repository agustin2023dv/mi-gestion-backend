package com.migestion.finance.application;

import com.migestion.finance.domain.GastoOperativo;
import com.migestion.finance.domain.GastoOperativoRepository;
import com.migestion.finance.dto.CreateGastoOperativoRequest;
import com.migestion.finance.dto.GastoOperativoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateGastoOperativoUseCase {

    private final GastoOperativoRepository gastoOperativoRepository;
    private final FinanceMapper financeMapper;

    public CreateGastoOperativoUseCase(
            GastoOperativoRepository gastoOperativoRepository,
            FinanceMapper financeMapper
    ) {
        this.gastoOperativoRepository = gastoOperativoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public GastoOperativoResponse execute(CreateGastoOperativoRequest request) {
        Long tenantId = requireTenantId();

        GastoOperativo gastoOperativo = financeMapper.toGastoOperativo(request);
        gastoOperativo.setTenantId(tenantId);

        GastoOperativo persistedGastoOperativo = gastoOperativoRepository.save(gastoOperativo);
        return financeMapper.toGastoOperativoResponse(persistedGastoOperativo);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
