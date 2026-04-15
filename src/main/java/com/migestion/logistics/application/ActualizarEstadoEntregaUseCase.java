package com.migestion.logistics.application;

import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.logistics.domain.event.EntregaStatusChangedEvent;
import com.migestion.logistics.dto.ActualizarEstadoEntregaRequest;
import com.migestion.logistics.dto.EntregaResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarEstadoEntregaUseCase {

    private final EntregaRepository entregaRepository;
    private final LogisticsMapper logisticsMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ActualizarEstadoEntregaUseCase(
            EntregaRepository entregaRepository,
            LogisticsMapper logisticsMapper,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.entregaRepository = entregaRepository;
        this.logisticsMapper = logisticsMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public EntregaResponse execute(
            Long entregaId,
            ActualizarEstadoEntregaRequest request,
            Long tenantId
    ) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            Entrega entrega = entregaRepository.findByIdAndTenantId(entregaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Entrega", entregaId));

            validateStateTransition(entrega.getEstado(), request.nuevoEstado());

            if (request.latitud() != null && request.longitud() != null) {
                entrega.setLatitudActual(request.latitud());
                entrega.setLongitudActual(request.longitud());
                entrega.setGeolocalizacionValidada(true);
            }

            entrega.setEstado(request.nuevoEstado());

            if ("EN_CAMINO".equalsIgnoreCase(request.nuevoEstado())) {
                entrega.setInicioEntrega(Instant.now());
            } else if ("ENTREGADA".equalsIgnoreCase(request.nuevoEstado())) {
                entrega.setEntregaConfirmada(Instant.now());
            }

            Entrega savedEntrega = entregaRepository.save(entrega);

            EntregaStatusChangedEvent event = EntregaStatusChangedEvent.builder()
                    .entregaId(savedEntrega.getId())
                    .pedidoId(savedEntrega.getPedidoId())
                    .repartidorId(savedEntrega.getRepartidorId())
                    .tenantId(tenantId)
                    .nuevoEstado(request.nuevoEstado())
                    .latitud(request.latitud())
                    .longitud(request.longitud())
                    .occurredAt(Instant.now())
                    .build();

            applicationEventPublisher.publishEvent(event);

            return logisticsMapper.toResponse(savedEntrega);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    private void validateStateTransition(String currentState, String newState) {
        if (currentState == null) {
            throw new BusinessRuleViolationException(
                    "INVALID_STATE_TRANSITION",
                    "Current delivery state is null"
            );
        }

        if ("ENTREGADA".equalsIgnoreCase(currentState)) {
            throw new BusinessRuleViolationException(
                    "INVALID_STATE_TRANSITION",
                    "Cannot transition from ENTREGADA state"
            );
        }

        if ("ASIGNADA".equalsIgnoreCase(currentState) &&
                (!("EN_CAMINO".equalsIgnoreCase(newState) || "CANCELADA".equalsIgnoreCase(newState)))) {
            throw new BusinessRuleViolationException(
                    "INVALID_STATE_TRANSITION",
                    "From ASIGNADA state, can only transition to EN_CAMINO or CANCELADA"
            );
        }

        if ("EN_CAMINO".equalsIgnoreCase(currentState) &&
                (!("ENTREGADA".equalsIgnoreCase(newState) || "CANCELADA".equalsIgnoreCase(newState)))) {
            throw new BusinessRuleViolationException(
                    "INVALID_STATE_TRANSITION",
                    "From EN_CAMINO state, can only transition to ENTREGADA or CANCELADA"
            );
        }
    }
}
