package com.migestion.logistics.application;

import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.logistics.domain.event.EntregaAssignedEvent;
import com.migestion.logistics.dto.AsignarRepartidorRequest;
import com.migestion.logistics.dto.EntregaResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsignarRepartidorUseCase {

    private final EntregaRepository entregaRepository;
    private final LogisticsMapper logisticsMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AsignarRepartidorUseCase(
            EntregaRepository entregaRepository,
            LogisticsMapper logisticsMapper,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.entregaRepository = entregaRepository;
        this.logisticsMapper = logisticsMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public EntregaResponse execute(Long entregaId, AsignarRepartidorRequest request, Long tenantId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            Entrega entrega = entregaRepository.findByIdAndTenantId(entregaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Entrega", entregaId));

            entrega.setRepartidorId(request.repartidorId());
            entrega.setEstado("ASIGNADA");
            entrega.setAsignadoEn(Instant.now());

            Entrega savedEntrega = entregaRepository.save(entrega);

            EntregaAssignedEvent event = EntregaAssignedEvent.builder()
                    .entregaId(savedEntrega.getId())
                    .pedidoId(savedEntrega.getPedidoId())
                    .repartidorId(request.repartidorId())
                    .tenantId(tenantId)
                    .occurredAt(Instant.now())
                    .build();

            applicationEventPublisher.publishEvent(event);

            return logisticsMapper.toResponse(savedEntrega);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }
}
