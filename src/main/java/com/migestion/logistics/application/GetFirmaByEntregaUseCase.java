package com.migestion.logistics.application;

import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.logistics.domain.FirmaDigital;
import com.migestion.logistics.domain.FirmaDigitalRepository;
import com.migestion.logistics.dto.FirmaEntregaResponse;
import com.migestion.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetFirmaByEntregaUseCase {

    private final EntregaRepository entregaRepository;
    private final FirmaDigitalRepository firmaDigitalRepository;

    public GetFirmaByEntregaUseCase(
            EntregaRepository entregaRepository,
            FirmaDigitalRepository firmaDigitalRepository
    ) {
        this.entregaRepository = entregaRepository;
        this.firmaDigitalRepository = firmaDigitalRepository;
    }

    @Transactional(readOnly = true)
    public FirmaEntregaResponse execute(Long entregaId, Long tenantId) {
        entregaRepository.findByIdAndTenantId(entregaId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega", entregaId));

        FirmaDigital firmaDigital = firmaDigitalRepository.findByEntregaId(entregaId)
                .orElseThrow(() -> new ResourceNotFoundException("FirmaDigital", "entregaId=" + entregaId));

        return FirmaEntregaResponse.builder()
                .token(firmaDigital.getTokenUnico())
                .estado(resolveEstado(firmaDigital))
                .firmaDatos(firmaDigital.getFirmaDatos())
                .dispositivoInfo(firmaDigital.getDispositivoInfo())
                .generadoEn(firmaDigital.getGeneradoEn())
                .firmadoEn(firmaDigital.getFirmadoEn())
                .build();
    }

    private String resolveEstado(FirmaDigital firmaDigital) {
        if (firmaDigital.getFirmadoEn() != null) {
            return "FIRMADO";
        }
        if (firmaDigital.isExpirado()) {
            return "EXPIRADO";
        }
        return "PENDIENTE";
    }
}