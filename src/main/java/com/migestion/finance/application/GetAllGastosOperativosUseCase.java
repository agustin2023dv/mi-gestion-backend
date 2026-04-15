package com.migestion.finance.application;

import com.migestion.finance.domain.GastoOperativo;
import com.migestion.finance.domain.GastoOperativoRepository;
import com.migestion.finance.dto.GastoOperativoFilterRequest;
import com.migestion.finance.dto.GastoOperativoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.dto.PageResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllGastosOperativosUseCase {

    private final GastoOperativoRepository gastoOperativoRepository;
    private final FinanceMapper financeMapper;

    public GetAllGastosOperativosUseCase(
            GastoOperativoRepository gastoOperativoRepository,
            FinanceMapper financeMapper
    ) {
        this.gastoOperativoRepository = gastoOperativoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<GastoOperativoResponse> execute(GastoOperativoFilterRequest filter, Pageable pageable) {
        Long tenantId = requireTenantId();

        Page<GastoOperativo> gastoOperativoPage = gastoOperativoRepository.findAllWithFilters(
                tenantId,
                filter.fechaDesde(),
                filter.fechaHasta(),
                filter.categoriaGastoId(),
                filter.centroCostoId(),
                filter.esProrrateable(),
                pageable
        );

        List<GastoOperativoResponse> content = gastoOperativoPage.getContent().stream()
                .map(financeMapper::toGastoOperativoResponse)
                .toList();

        return PageResponse.<GastoOperativoResponse>builder()
                .content(content)
                .pageNumber(gastoOperativoPage.getNumber())
                .pageSize(gastoOperativoPage.getSize())
                .totalElements(gastoOperativoPage.getTotalElements())
                .totalPages(gastoOperativoPage.getTotalPages())
                .hasNext(gastoOperativoPage.hasNext())
                .hasPrevious(gastoOperativoPage.hasPrevious())
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
