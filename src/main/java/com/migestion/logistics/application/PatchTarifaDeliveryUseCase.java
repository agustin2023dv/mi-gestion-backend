package com.migestion.logistics.application;

import com.migestion.logistics.domain.TarifaDelivery;
import com.migestion.logistics.domain.TarifaDeliveryRepository;
import com.migestion.logistics.dto.TarifaDeliveryPatchRequest;
import com.migestion.logistics.dto.TarifaDeliveryResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatchTarifaDeliveryUseCase {

    private final TarifaDeliveryRepository tarifaDeliveryRepository;
    private final LogisticsMapper logisticsMapper;

    public PatchTarifaDeliveryUseCase(
            TarifaDeliveryRepository tarifaDeliveryRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.tarifaDeliveryRepository = tarifaDeliveryRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional
    public TarifaDeliveryResponse execute(Long tarifaId, Long tenantId, TarifaDeliveryPatchRequest request) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            TarifaDelivery tarifaDelivery = tarifaDeliveryRepository.findByIdAndTenantId(tarifaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("TarifaDelivery", tarifaId));

            if (request.nombre() != null) {
                tarifaDelivery.setNombre(request.nombre());
            }
            if (request.tipoCalculo() != null) {
                tarifaDelivery.setTipoCalculo(request.tipoCalculo());
            }
            if (request.precioBase() != null) {
                tarifaDelivery.setPrecioBase(request.precioBase());
            }
            if (request.precioPorKm() != null) {
                tarifaDelivery.setPrecioPorKm(request.precioPorKm());
            }
            if (request.distanciaMinimaKm() != null) {
                tarifaDelivery.setDistanciaMinimaKm(request.distanciaMinimaKm());
            }
            if (request.distanciaMaximaKm() != null) {
                tarifaDelivery.setDistanciaMaximaKm(request.distanciaMaximaKm());
            }
            if (request.isActive() != null) {
                tarifaDelivery.setActive(request.isActive());
            }

            validateDistanceRange(
                    tarifaDelivery.getDistanciaMinimaKm(),
                    tarifaDelivery.getDistanciaMaximaKm()
            );

            TarifaDelivery updatedTarifa = tarifaDeliveryRepository.save(tarifaDelivery);
            return logisticsMapper.toResponse(updatedTarifa);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    private void validateDistanceRange(BigDecimal distanciaMinimaKm, BigDecimal distanciaMaximaKm) {
        BigDecimal minima = distanciaMinimaKm == null ? BigDecimal.ZERO : distanciaMinimaKm;
        if (distanciaMaximaKm != null && distanciaMaximaKm.compareTo(minima) < 0) {
            throw new BusinessRuleViolationException(
                    "TARIFA_RANGO_INVALIDO",
                    "distanciaMaximaKm cannot be less than distanciaMinimaKm"
            );
        }
    }
}
