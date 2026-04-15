package com.migestion.finance.application;

import com.migestion.finance.domain.CategoriaGastoRepository;
import com.migestion.finance.dto.CategoriaGastoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllCategoriasGastoUseCase {

    private final CategoriaGastoRepository categoriaGastoRepository;
    private final FinanceMapper financeMapper;

    public GetAllCategoriasGastoUseCase(
            CategoriaGastoRepository categoriaGastoRepository,
            FinanceMapper financeMapper
    ) {
        this.categoriaGastoRepository = categoriaGastoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoriaGastoResponse> execute() {
        Long tenantId = requireTenantId();

        return categoriaGastoRepository.findAllByTenantId(tenantId)
                .stream()
                .map(financeMapper::toCategoriaGastoResponse)
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