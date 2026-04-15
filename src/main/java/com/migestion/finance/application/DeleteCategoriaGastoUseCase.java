package com.migestion.finance.application;

import com.migestion.finance.domain.CategoriaGasto;
import com.migestion.finance.domain.CategoriaGastoRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCategoriaGastoUseCase {

    private final CategoriaGastoRepository categoriaGastoRepository;

    public DeleteCategoriaGastoUseCase(CategoriaGastoRepository categoriaGastoRepository) {
        this.categoriaGastoRepository = categoriaGastoRepository;
    }

    @Transactional
    public void execute(Long categoriaGastoId) {
        Long tenantId = requireTenantId();

        CategoriaGasto categoriaGasto = categoriaGastoRepository.findByIdAndTenantId(categoriaGastoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaGasto", categoriaGastoId));

        categoriaGasto.setDeletedAt(Instant.now());
        categoriaGastoRepository.save(categoriaGasto);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}