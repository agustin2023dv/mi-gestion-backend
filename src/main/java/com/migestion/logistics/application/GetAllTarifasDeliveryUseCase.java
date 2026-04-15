package com.migestion.logistics.application;

import com.migestion.logistics.domain.TarifaDeliveryRepository;
import com.migestion.logistics.dto.TarifaDeliveryResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.security.TenantContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllTarifasDeliveryUseCase {

    private final TarifaDeliveryRepository tarifaDeliveryRepository;
    private final LogisticsMapper logisticsMapper;

    public GetAllTarifasDeliveryUseCase(
            TarifaDeliveryRepository tarifaDeliveryRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.tarifaDeliveryRepository = tarifaDeliveryRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional(readOnly = true)
    public List<TarifaDeliveryResponse> execute(Long tenantId, Boolean isActive) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            return tarifaDeliveryRepository.findAllByTenantId(tenantId).stream()
                    .filter(tarifa -> isActive == null || tarifa.isActive() == isActive)
                    .map(logisticsMapper::toResponse)
                    .toList();
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }
}
