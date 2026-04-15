package com.migestion.finance.application;

import com.migestion.finance.domain.ConfiguracionContabilidadCostos;
import com.migestion.finance.domain.ConfiguracionContabilidadCostosRepository;
import com.migestion.finance.dto.ConfiguracionContabilidadCostosRequest;
import com.migestion.finance.dto.ConfiguracionContabilidadCostosResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateConfiguracionContabilidadCostosUseCase {

    private final ConfiguracionContabilidadCostosRepository configuracionContabilidadCostosRepository;
    private final FinanceMapper financeMapper;

    public UpdateConfiguracionContabilidadCostosUseCase(
            ConfiguracionContabilidadCostosRepository configuracionContabilidadCostosRepository,
            FinanceMapper financeMapper
    ) {
        this.configuracionContabilidadCostosRepository = configuracionContabilidadCostosRepository;
        this.financeMapper = financeMapper;
    }

    @Transactional
    public ConfiguracionContabilidadCostosResponse executePut(ConfiguracionContabilidadCostosRequest request) {
        validateFullUpdate(request);
        return update(request, false);
    }

    @Transactional
    public ConfiguracionContabilidadCostosResponse executePatch(ConfiguracionContabilidadCostosRequest request) {
        return update(request, true);
    }

    private ConfiguracionContabilidadCostosResponse update(
            ConfiguracionContabilidadCostosRequest request,
            boolean partialUpdate
    ) {
        Long tenantId = requireTenantId();

        ConfiguracionContabilidadCostos configuracion = configuracionContabilidadCostosRepository.findByTenantId(tenantId)
                .orElseGet(() -> ConfiguracionContabilidadCostos.builder()
                        .tenantId(tenantId)
                        .build());

        if (request.incluyeManoObraDirectaEnCosto() != null || !partialUpdate) {
            configuracion.setIncluyeManoObraDirectaEnCosto(Boolean.TRUE.equals(request.incluyeManoObraDirectaEnCosto()));
        }
        if (request.incluyeCifEnCosto() != null || !partialUpdate) {
            configuracion.setIncluyeCifEnCosto(Boolean.TRUE.equals(request.incluyeCifEnCosto()));
        }
        if (request.incluyeImpuestosEnCosto() != null || !partialUpdate) {
            configuracion.setIncluyeImpuestosEnCosto(Boolean.TRUE.equals(request.incluyeImpuestosEnCosto()));
        }
        if (request.metodoValoracionInventario() != null || !partialUpdate) {
            configuracion.setMetodoValoracionInventario(request.metodoValoracionInventario());
        }
        if (request.monedaFuncional() != null || !partialUpdate) {
            configuracion.setMonedaFuncional(request.monedaFuncional());
        }

        ConfiguracionContabilidadCostos persisted = configuracionContabilidadCostosRepository.save(configuracion);
        return financeMapper.toConfiguracionContabilidadCostosResponse(persisted);
    }

    private void validateFullUpdate(ConfiguracionContabilidadCostosRequest request) {
        if (request.incluyeManoObraDirectaEnCosto() == null
                || request.incluyeCifEnCosto() == null
                || request.incluyeImpuestosEnCosto() == null
                || request.metodoValoracionInventario() == null
                || request.monedaFuncional() == null) {
            throw new BusinessRuleViolationException(
                    "CONFIGURACION_CONTABILIDAD_INVALIDA",
                    "All fields are required for full update"
            );
        }
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }
}
