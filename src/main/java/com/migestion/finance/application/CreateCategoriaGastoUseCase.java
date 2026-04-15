package com.migestion.finance.application;

import com.migestion.finance.domain.CategoriaGasto;
import com.migestion.finance.domain.CategoriaGastoRepository;
import com.migestion.finance.dto.CategoriaGastoRequest;
import com.migestion.finance.dto.CategoriaGastoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCategoriaGastoUseCase {

    private final CategoriaGastoRepository categoriaGastoRepository;
    private final FinanceMapper financeMapper;

    public CreateCategoriaGastoUseCase(
            CategoriaGastoRepository categoriaGastoRepository,
            FinanceMapper financeMapper
    ) {
        this.categoriaGastoRepository = categoriaGastoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public CategoriaGastoResponse execute(CategoriaGastoRequest request) {
        Long tenantId = requireTenantId();

        CategoriaGasto categoriaGasto = financeMapper.toCategoriaGasto(request);
        categoriaGasto.setTenantId(tenantId);
        categoriaGasto.setEsDirecto(request.esDirecto() == null || request.esDirecto());
        categoriaGasto.setEsProrrateable(Boolean.TRUE.equals(request.esProrrateable()));

        CategoriaGasto persistedCategoriaGasto = categoriaGastoRepository.save(categoriaGasto);
        return financeMapper.toCategoriaGastoResponse(persistedCategoriaGasto);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}