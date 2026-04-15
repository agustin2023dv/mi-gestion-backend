package com.migestion.finance.application;

import com.migestion.finance.domain.AsignacionCostoIndirecto;
import com.migestion.finance.domain.AsignacionCostoIndirectoRepository;
import com.migestion.finance.domain.ProrrateoService;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EjecutarProrrateoUseCase {

    private final ProrrateoService prorrateoService;
    private final AsignacionCostoIndirectoRepository asignacionCostoIndirectoRepository;

    public EjecutarProrrateoUseCase(
            ProrrateoService prorrateoService,
            AsignacionCostoIndirectoRepository asignacionCostoIndirectoRepository
    ) {
        this.prorrateoService = prorrateoService;
        this.asignacionCostoIndirectoRepository = asignacionCostoIndirectoRepository;
    }

    @Transactional
    public List<AsignacionCostoIndirecto> execute(Instant periodoInicio, Instant periodoFin, Long criterioId) {
        Long tenantId = requireTenantId();

        List<AsignacionCostoIndirecto> preview = prorrateoService.calcularProrrateo(
                tenantId,
                periodoInicio,
                periodoFin,
                criterioId
        );

        asignacionCostoIndirectoRepository.deleteByTenantIdAndPeriodoInicioAndPeriodoFin(
                tenantId,
                periodoInicio,
                periodoFin
        );

        if (preview.isEmpty()) {
            return List.of();
        }

        return asignacionCostoIndirectoRepository.saveAll(preview);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
