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
public class GetEntregasPendientesUseCase {

    private final EntregaRepository entregaRepository;
    private final LogisticsMapper logisticsMapper;

    public GetEntregasPendientesUseCase(
            EntregaRepository entregaRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.entregaRepository = entregaRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional(readOnly = true)
    public List<EntregaResponse> execute(Long repartidorId, Long tenantId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            List<Entrega> entregas = entregaRepository.findAllByTenantId(tenantId).stream()
                    .filter(e -> e.getRepartidorId() != null && e.getRepartidorId().equals(repartidorId))
                    .filter(e -> !e.getEstado().equals("ENTREGADA"))
                    .toList();

            return entregas.stream()
                    .map(logisticsMapper::toResponse)
                    .toList();
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }
}
