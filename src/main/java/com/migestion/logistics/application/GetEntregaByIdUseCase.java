package com.migestion.logistics.application;

import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.logistics.dto.EntregaResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetEntregaByIdUseCase {

    private final EntregaRepository entregaRepository;
    private final LogisticsMapper logisticsMapper;

    public GetEntregaByIdUseCase(
            EntregaRepository entregaRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.entregaRepository = entregaRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional(readOnly = true)
    public EntregaResponse execute(Long entregaId, Long tenantId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            Entrega entrega = entregaRepository.findByIdAndTenantId(entregaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Entrega", entregaId));

            return logisticsMapper.toResponse(entrega);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }
}
