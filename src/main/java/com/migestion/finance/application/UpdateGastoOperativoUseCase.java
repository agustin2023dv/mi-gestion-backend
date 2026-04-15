package com.migestion.finance.application;

import com.migestion.finance.domain.GastoOperativo;
import com.migestion.finance.domain.GastoOperativoRepository;
import com.migestion.finance.dto.GastoOperativoResponse;
import com.migestion.finance.dto.UpdateGastoOperativoRequest;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateGastoOperativoUseCase {

    private final GastoOperativoRepository gastoOperativoRepository;
    private final FinanceMapper financeMapper;

    public UpdateGastoOperativoUseCase(
            GastoOperativoRepository gastoOperativoRepository,
            FinanceMapper financeMapper
    ) {
        this.gastoOperativoRepository = gastoOperativoRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public GastoOperativoResponse execute(Long gastoOperativoId, UpdateGastoOperativoRequest request) {
        Long tenantId = requireTenantId();

        GastoOperativo gastoOperativo = gastoOperativoRepository.findByIdAndTenantId(gastoOperativoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("GastoOperativo", gastoOperativoId));

        if (request.nombre() != null) {
            gastoOperativo.setNombre(request.nombre());
        }
        if (request.monto() != null) {
            gastoOperativo.setMonto(request.monto());
        }
        if (request.fechaRegistro() != null) {
            gastoOperativo.setFechaRegistro(request.fechaRegistro());
        }
        if (request.empleadoId() != null) {
            gastoOperativo.setEmpleadoId(request.empleadoId());
        }
        if (request.centroCostoId() != null) {
            gastoOperativo.setCentroCostoId(request.centroCostoId());
        }
        if (request.categoriaGastoId() != null) {
            gastoOperativo.setCategoriaGastoId(request.categoriaGastoId());
        }
        if (request.periodicidad() != null) {
            gastoOperativo.setPeriodicidad(request.periodicidad());
        }
        if (request.esRecurrente() != null) {
            gastoOperativo.setEsRecurrente(request.esRecurrente());
        }
        if (request.esDirecto() != null) {
            gastoOperativo.setEsDirecto(request.esDirecto());
        }
        if (request.esProrrateable() != null) {
            gastoOperativo.setEsProrrateable(request.esProrrateable());
        }
        if (request.descripcion() != null) {
            gastoOperativo.setDescripcion(request.descripcion());
        }

        GastoOperativo updatedGastoOperativo = gastoOperativoRepository.save(gastoOperativo);
        return financeMapper.toGastoOperativoResponse(updatedGastoOperativo);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
