package com.migestion.logistics.application;

import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.logistics.dto.EntregaResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListEntregasTenantUseCase {

    private final EntregaRepository entregaRepository;
    private final LogisticsMapper logisticsMapper;

    public ListEntregasTenantUseCase(
            EntregaRepository entregaRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.entregaRepository = entregaRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional(readOnly = true)
    public List<EntregaResponse> execute(Long tenantId, String estado, Long repartidorId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            List<Entrega> entregas = entregaRepository.findAllByTenantId(tenantId);

            return entregas.stream()
                    .filter(e -> estado == null || e.getEstado().equalsIgnoreCase(estado))
                    .filter(e -> repartidorId == null || (e.getRepartidorId() != null && e.getRepartidorId().equals(repartidorId)))
                    .map(logisticsMapper::toResponse)
                    .toList();
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    @Transactional(readOnly = true)
    public List<EntregaResponse> executeAll(Long tenantId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            return entregaRepository.findAllByTenantId(tenantId).stream()
                    .map(logisticsMapper::toResponse)
                    .toList();
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }
}
