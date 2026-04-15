package com.migestion.finance.application;

import com.migestion.finance.domain.ConfiguracionContabilidadCostos;
import com.migestion.finance.domain.ConfiguracionContabilidadCostosRepository;
import com.migestion.finance.dto.ConfiguracionContabilidadCostosResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetConfiguracionContabilidadCostosUseCase {

    private final ConfiguracionContabilidadCostosRepository configuracionContabilidadCostosRepository;
    private final FinanceMapper financeMapper;

    public GetConfiguracionContabilidadCostosUseCase(
            ConfiguracionContabilidadCostosRepository configuracionContabilidadCostosRepository,
            FinanceMapper financeMapper
    ) {
        this.configuracionContabilidadCostosRepository = configuracionContabilidadCostosRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional(readOnly = true)
    public ConfiguracionContabilidadCostosResponse execute() {
        Long tenantId = requireTenantId();

        ConfiguracionContabilidadCostos configuracion = configuracionContabilidadCostosRepository.findByTenantId(tenantId)
                .orElseGet(() -> ConfiguracionContabilidadCostos.builder()
                        .tenantId(tenantId)
                        .build());

        return financeMapper.toConfiguracionContabilidadCostosResponse(configuracion);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
