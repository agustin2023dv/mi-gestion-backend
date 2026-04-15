package com.migestion.logistics.application;

import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.logistics.domain.FirmaDigital;
import com.migestion.logistics.domain.FirmaDigitalRepository;
import com.migestion.logistics.dto.FirmaResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenerateFirmaDigitalUseCase {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(5);
    private static final String FIRMA_PUBLIC_URL_PREFIX = "https://mi-gestion.com/firmar/";

    private final EntregaRepository entregaRepository;
    private final FirmaDigitalRepository firmaDigitalRepository;

    public GenerateFirmaDigitalUseCase(
            EntregaRepository entregaRepository,
            FirmaDigitalRepository firmaDigitalRepository
    ) {
        this.entregaRepository = entregaRepository;
        this.firmaDigitalRepository = firmaDigitalRepository;
    }

    @Transactional
    public FirmaResponse execute(Long entregaId, Long repartidorId, Long tenantId) {
        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            Entrega entrega = entregaRepository.findByIdAndTenantId(entregaId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Entrega", entregaId));

            validateCourierAssignment(entrega, repartidorId);

            UUID token = UUID.randomUUID();
            String qrCodeUrl = FIRMA_PUBLIC_URL_PREFIX + token;
            Instant generadoEn = Instant.now();

            FirmaDigital firmaDigital = firmaDigitalRepository.findByEntregaId(entregaId)
                    .orElseGet(FirmaDigital::new);

            firmaDigital.setEntregaId(entregaId);
            firmaDigital.setPedidoId(entrega.getPedidoId());
            firmaDigital.setTokenUnico(token);
            firmaDigital.setQrCodeData(qrCodeUrl);
            firmaDigital.setFirmaDatos(null);
            firmaDigital.setDispositivoInfo(null);
            firmaDigital.setGeneradoEn(generadoEn);
            firmaDigital.setFirmadoEn(null);
            firmaDigital.setExpirado(false);

            FirmaDigital savedFirma = firmaDigitalRepository.save(firmaDigital);

            return FirmaResponse.builder()
                    .token(savedFirma.getTokenUnico())
                    .qrCodeData(savedFirma.getQrCodeData())
                    .qrCodeUrl(qrCodeUrl)
                    .expiracion(generadoEn.plus(TOKEN_TTL))
                    .build();
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    private void validateCourierAssignment(Entrega entrega, Long repartidorId) {
        if (entrega.getRepartidorId() == null) {
            throw new BusinessRuleViolationException(
                    "ENTREGA_SIN_REPARTIDOR",
                    "The delivery does not have an assigned courier"
            );
        }

        if (!entrega.getRepartidorId().equals(repartidorId)) {
            throw new BusinessRuleViolationException(
                    "ENTREGA_NO_ASIGNADA_A_REPARTIDOR",
                    "The delivery is assigned to a different courier"
            );
        }
    }
}