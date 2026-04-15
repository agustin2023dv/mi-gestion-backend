package com.migestion.logistics.application;

import com.migestion.logistics.domain.TarifaDelivery;
import com.migestion.logistics.domain.TarifaDeliveryRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalcularCostoEnvioUseCase {

    private static final BigDecimal MAX_DISTANCE = new BigDecimal("999999");

    private final TarifaDeliveryRepository tarifaDeliveryRepository;

    public CalcularCostoEnvioUseCase(TarifaDeliveryRepository tarifaDeliveryRepository) {
        this.tarifaDeliveryRepository = tarifaDeliveryRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal execute(Long tenantId, BigDecimal distanciaKm) {
        validateDistance(distanciaKm);

        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            TarifaDelivery tarifa = tarifaDeliveryRepository.findAllByTenantId(tenantId).stream()
                    .filter(TarifaDelivery::isActive)
                    .filter(candidate -> isInRange(distanciaKm, candidate))
                    .min(Comparator.comparing(this::maxDistanceForOrder))
                    .orElseThrow(() -> new BusinessRuleViolationException(
                            "SIN_COBERTURA",
                            "No active delivery rate covers the requested distance"
                    ));

            return calculateCost(tarifa, distanciaKm);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    private void validateDistance(BigDecimal distanciaKm) {
        if (distanciaKm == null || distanciaKm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException(
                    "DISTANCIA_INVALIDA",
                    "distanciaKm must be greater than 0"
            );
        }
    }

    private boolean isInRange(BigDecimal distanciaKm, TarifaDelivery tarifaDelivery) {
        BigDecimal distanciaMinima = tarifaDelivery.getDistanciaMinimaKm() == null
                ? BigDecimal.ZERO
                : tarifaDelivery.getDistanciaMinimaKm();
        BigDecimal distanciaMaxima = tarifaDelivery.getDistanciaMaximaKm();

        boolean withinMin = distanciaKm.compareTo(distanciaMinima) >= 0;
        boolean withinMax = distanciaMaxima == null || distanciaKm.compareTo(distanciaMaxima) <= 0;

        return withinMin && withinMax;
    }

    private BigDecimal maxDistanceForOrder(TarifaDelivery tarifaDelivery) {
        return tarifaDelivery.getDistanciaMaximaKm() == null
                ? MAX_DISTANCE
                : tarifaDelivery.getDistanciaMaximaKm();
    }

    private BigDecimal calculateCost(TarifaDelivery tarifaDelivery, BigDecimal distanciaKm) {
        BigDecimal precioBase = tarifaDelivery.getPrecioBase() == null
                ? BigDecimal.ZERO
                : tarifaDelivery.getPrecioBase();
        BigDecimal precioPorKm = tarifaDelivery.getPrecioPorKm() == null
                ? BigDecimal.ZERO
                : tarifaDelivery.getPrecioPorKm();

        if ("fijo".equalsIgnoreCase(tarifaDelivery.getTipoCalculo())) {
            return precioBase.setScale(2, RoundingMode.HALF_UP);
        }

        return precioBase
                .add(precioPorKm.multiply(distanciaKm))
                .setScale(2, RoundingMode.HALF_UP);
    }
}