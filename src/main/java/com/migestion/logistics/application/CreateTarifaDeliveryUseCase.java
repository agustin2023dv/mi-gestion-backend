package com.migestion.logistics.application;

import com.migestion.logistics.domain.TarifaDelivery;
import com.migestion.logistics.domain.TarifaDeliveryRepository;
import com.migestion.logistics.dto.TarifaDeliveryRequest;
import com.migestion.logistics.dto.TarifaDeliveryResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTarifaDeliveryUseCase {

    private final TarifaDeliveryRepository tarifaDeliveryRepository;
    private final LogisticsMapper logisticsMapper;

    public CreateTarifaDeliveryUseCase(
            TarifaDeliveryRepository tarifaDeliveryRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.tarifaDeliveryRepository = tarifaDeliveryRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional
    public TarifaDeliveryResponse execute(Long tenantId, TarifaDeliveryRequest request) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            TarifaDelivery tarifaDelivery = logisticsMapper.toEntity(request);

            tarifaDelivery.setTenantId(tenantId);
            tarifaDelivery.setDistanciaMinimaKm(defaultDistance(request.distanciaMinimaKm()));
            tarifaDelivery.setDistanciaMaximaKm(request.distanciaMaximaKm());
            tarifaDelivery.setActive(request.isActive() == null || request.isActive());

            validateDistanceRange(
                    tarifaDelivery.getDistanciaMinimaKm(),
                    tarifaDelivery.getDistanciaMaximaKm()
            );

            TarifaDelivery savedTarifa = tarifaDeliveryRepository.save(tarifaDelivery);
            return logisticsMapper.toResponse(savedTarifa);
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