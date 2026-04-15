package com.migestion.logistics.application;

import com.migestion.logistics.domain.ComprobanteEntrega;
import com.migestion.logistics.domain.ComprobanteEntregaRepository;
import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.logistics.dto.ComprobanteResponse;
import com.migestion.logistics.infrastructure.mapper.LogisticsMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetComprobanteUseCase {

    private final ComprobanteEntregaRepository comprobanteEntregaRepository;
    private final EntregaRepository entregaRepository;
    private final LogisticsMapper logisticsMapper;

    public GetComprobanteUseCase(
            ComprobanteEntregaRepository comprobanteEntregaRepository,
            EntregaRepository entregaRepository,
            LogisticsMapper logisticsMapper
    ) {
        this.comprobanteEntregaRepository = comprobanteEntregaRepository;
        this.entregaRepository = entregaRepository;
        this.logisticsMapper = logisticsMapper;
    }

    @Transactional
    public ComprobanteResponse execute(Long entregaId, Long tenantId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            Entrega entrega = entregaRepository.findByIdAndTenantId(entregaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Entrega", entregaId));

            ComprobanteEntrega comprobante = comprobanteEntregaRepository.findByEntregaId(entregaId)
                    .orElse(null);

            if (comprobante == null) {
                comprobante = ComprobanteEntrega.builder()
                        .entregaId(entregaId)
                        .pedidoId(entrega.getPedidoId())
                        .hashCriptografico(generateHash())
                        .fechaEntrega(Instant.now())
                        .build();

                comprobante = comprobanteEntregaRepository.save(comprobante);
            }

            return logisticsMapper.toResponse(comprobante);
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    private String generateHash() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
