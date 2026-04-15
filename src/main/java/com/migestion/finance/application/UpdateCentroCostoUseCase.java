package com.migestion.finance.application;

import com.migestion.finance.domain.CentroCosto;
import com.migestion.finance.domain.CentroCostoRepository;
import com.migestion.finance.dto.CentroCostoRequest;
import com.migestion.finance.dto.CentroCostoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCentroCostoUseCase {

    private final CentroCostoRepository centroCostoRepository;
    private final FinanceMapper financeMapper;

    public UpdateCentroCostoUseCase(
            CentroCostoRepository centroCostoRepository,
            FinanceMapper financeMapper
    ) {
        this.centroCostoRepository = centroCostoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public CentroCostoResponse execute(Long centroCostoId, CentroCostoRequest request) {
        Long tenantId = requireTenantId();

        CentroCosto centroCosto = centroCostoRepository.findByIdAndTenantId(centroCostoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CentroCosto", centroCostoId));

        if (request.codigo() != null) {
            centroCosto.setCodigo(request.codigo());
        }
        if (request.nombre() != null) {
            centroCosto.setNombre(request.nombre());
        }
        if (request.descripcion() != null) {
            centroCosto.setDescripcion(request.descripcion());
        }
        if (request.isActive() != null) {
            centroCosto.setActive(request.isActive());
        }

        CentroCosto persistedCentroCosto = centroCostoRepository.save(centroCosto);
        return financeMapper.toCentroCostoResponse(persistedCentroCosto);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}