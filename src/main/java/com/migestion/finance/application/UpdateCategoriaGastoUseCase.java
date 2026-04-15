package com.migestion.finance.application;

import com.migestion.finance.domain.CategoriaGasto;
import com.migestion.finance.domain.CategoriaGastoRepository;
import com.migestion.finance.dto.CategoriaGastoRequest;
import com.migestion.finance.dto.CategoriaGastoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCategoriaGastoUseCase {

    private final CategoriaGastoRepository categoriaGastoRepository;
    private final FinanceMapper financeMapper;

    public UpdateCategoriaGastoUseCase(
            CategoriaGastoRepository categoriaGastoRepository,
            FinanceMapper financeMapper
    ) {
        this.categoriaGastoRepository = categoriaGastoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public CategoriaGastoResponse execute(Long categoriaGastoId, CategoriaGastoRequest request) {
        Long tenantId = requireTenantId();

        CategoriaGasto categoriaGasto = categoriaGastoRepository.findByIdAndTenantId(categoriaGastoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaGasto", categoriaGastoId));

        if (request.nombre() != null) {
            categoriaGasto.setNombre(request.nombre());
        }
        if (request.tipoNaturaleza() != null) {
            categoriaGasto.setTipoNaturaleza(request.tipoNaturaleza());
        }
        if (request.esDirecto() != null) {
            categoriaGasto.setEsDirecto(request.esDirecto());
        }
        if (request.esProrrateable() != null) {
            categoriaGasto.setEsProrrateable(request.esProrrateable());
        }
        if (request.descripcion() != null) {
            categoriaGasto.setDescripcion(request.descripcion());
        }

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