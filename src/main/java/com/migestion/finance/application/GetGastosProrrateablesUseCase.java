package com.migestion.finance.application;

import com.migestion.finance.domain.GastoOperativo;
import com.migestion.finance.domain.GastoOperativoRepository;
import com.migestion.finance.dto.GastoOperativoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetGastosProrrateablesUseCase {

    private final GastoOperativoRepository gastoOperativoRepository;
    private final FinanceMapper financeMapper;

    public GetGastosProrrateablesUseCase(
            GastoOperativoRepository gastoOperativoRepository,
            FinanceMapper financeMapper
    ) {
        this.gastoOperativoRepository = gastoOperativoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional(readOnly = true)
    public List<GastoOperativoResponse> execute(Instant fechaDesde, Instant fechaHasta) {
        Long tenantId = requireTenantId();

        List<GastoOperativo> gastosProrrateables = gastoOperativoRepository.findProrrateableGastos(
                tenantId,
                fechaDesde,
                fechaHasta
        );

        return gastosProrrateables.stream()
                .map(financeMapper::toGastoOperativoResponse)
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
