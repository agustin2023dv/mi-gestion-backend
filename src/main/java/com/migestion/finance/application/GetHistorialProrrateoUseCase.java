package com.migestion.finance.application;

import com.migestion.finance.domain.AsignacionCostoIndirectoRepository;
import com.migestion.finance.dto.HistorialProrrateoResponse;
import com.migestion.shared.dto.PageResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetHistorialProrrateoUseCase {

    private final AsignacionCostoIndirectoRepository asignacionCostoIndirectoRepository;

    public GetHistorialProrrateoUseCase(AsignacionCostoIndirectoRepository asignacionCostoIndirectoRepository) {
        this.asignacionCostoIndirectoRepository = asignacionCostoIndirectoRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<HistorialProrrateoResponse> execute(Pageable pageable) {
        Long tenantId = requireTenantId();
        Page<HistorialProrrateoResponse> historialPage = asignacionCostoIndirectoRepository
                .findHistorialByTenantId(tenantId, pageable);

        return PageResponse.<HistorialProrrateoResponse>builder()
                .content(historialPage.getContent())
                .pageNumber(historialPage.getNumber())
                .pageSize(historialPage.getSize())
                .totalElements(historialPage.getTotalElements())
                .totalPages(historialPage.getTotalPages())
                .hasNext(historialPage.hasNext())
                .hasPrevious(historialPage.hasPrevious())
                .build();
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
