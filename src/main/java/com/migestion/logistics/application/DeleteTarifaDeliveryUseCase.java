package com.migestion.logistics.application;

import com.migestion.logistics.domain.TarifaDelivery;
import com.migestion.logistics.domain.TarifaDeliveryRepository;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteTarifaDeliveryUseCase {

    private final TarifaDeliveryRepository tarifaDeliveryRepository;

    public DeleteTarifaDeliveryUseCase(TarifaDeliveryRepository tarifaDeliveryRepository) {
        this.tarifaDeliveryRepository = tarifaDeliveryRepository;
    }

    @Transactional
    public void execute(Long tarifaId, Long tenantId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            TarifaDelivery tarifaDelivery = tarifaDeliveryRepository.findByIdAndTenantId(tarifaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("TarifaDelivery", tarifaId));

            tarifaDelivery.setDeletedAt(Instant.now());
            tarifaDeliveryRepository.save(tarifaDelivery);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }
}
