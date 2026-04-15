package com.migestion.logistics.application;

import com.migestion.logistics.domain.TarifaDelivery;
import com.migestion.logistics.domain.TarifaDeliveryRepository;
import com.migestion.logistics.dto.TarifaDeliveryResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetTarifaDeliveryByIdUseCase {

    private final TarifaDeliveryRepository tarifaDeliveryRepository;
    private final LogisticsMapper logisticsMapper;

    public GetTarifaDeliveryByIdUseCase(
            TarifaDeliveryRepository tarifaDeliveryRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.tarifaDeliveryRepository = tarifaDeliveryRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional(readOnly = true)
    public TarifaDeliveryResponse execute(Long tarifaId, Long tenantId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            TarifaDelivery tarifaDelivery = tarifaDeliveryRepository.findByIdAndTenantId(tarifaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("TarifaDelivery", tarifaId));

            return logisticsMapper.toResponse(tarifaDelivery);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }
}
