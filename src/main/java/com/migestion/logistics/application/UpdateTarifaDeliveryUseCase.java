package com.migestion.logistics.application;

import com.migestion.logistics.domain.TarifaDelivery;
import com.migestion.logistics.domain.TarifaDeliveryRepository;
import com.migestion.logistics.dto.TarifaDeliveryRequest;
import com.migestion.logistics.dto.TarifaDeliveryResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTarifaDeliveryUseCase {

    private final TarifaDeliveryRepository tarifaDeliveryRepository;
    private final LogisticsMapper logisticsMapper;

    public UpdateTarifaDeliveryUseCase(
            TarifaDeliveryRepository tarifaDeliveryRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.tarifaDeliveryRepository = tarifaDeliveryRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional
    public TarifaDeliveryResponse execute(Long tarifaId, Long tenantId, TarifaDeliveryRequest request) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            TarifaDelivery tarifaDelivery = tarifaDeliveryRepository.findByIdAndTenantId(tarifaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("TarifaDelivery", tarifaId));

            tarifaDelivery.setNombre(request.nombre());
            tarifaDelivery.setTipoCalculo(request.tipoCalculo());
            tarifaDelivery.setPrecioBase(request.precioBase());
            tarifaDelivery.setPrecioPorKm(request.precioPorKm());
            tarifaDelivery.setDistanciaMinimaKm(defaultDistance(request.distanciaMinimaKm()));
            tarifaDelivery.setDistanciaMaximaKm(request.distanciaMaximaKm());

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

    private BigDecimal defaultDistance(BigDecimal distanciaMinimaKm) {
        return distanciaMinimaKm == null ? BigDecimal.ZERO : distanciaMinimaKm;
    }

    private void validateDistanceRange(BigDecimal distanciaMinimaKm, BigDecimal distanciaMaximaKm) {
        if (distanciaMaximaKm != null && distanciaMaximaKm.compareTo(distanciaMinimaKm) < 0) {
            throw new BusinessRuleViolationException(
                    "TARIFA_RANGO_INVALIDO",
                    "distanciaMaximaKm cannot be less than distanciaMinimaKm"
            );
        }
    }
}
