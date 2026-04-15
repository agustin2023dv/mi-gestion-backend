package com.migestion.logistics.application;

import com.migestion.logistics.domain.FirmaDigital;
import com.migestion.logistics.domain.FirmaDigitalRepository;
import com.migestion.logistics.dto.VerifyFirmaResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyFirmaUseCase {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(5);

    private final FirmaDigitalRepository firmaDigitalRepository;

    public VerifyFirmaUseCase(FirmaDigitalRepository firmaDigitalRepository) {
        this.firmaDigitalRepository = firmaDigitalRepository;
    }

    @Transactional
    public VerifyFirmaResponse execute(String token, String firmaDatos, Map<String, Object> dispositivoInfo) {
        UUID tokenUnico = parseToken(token);

        FirmaDigital firmaDigital = firmaDigitalRepository.findByTokenUnico(tokenUnico)
                .orElseThrow(() -> new ResourceNotFoundException("FirmaDigital", token));

        if (firmaDigital.getFirmadoEn() != null) {
            throw new BusinessRuleViolationException(
                    "TOKEN_YA_UTILIZADO",
                    "The signature token was already used"
            );
        }

        Instant expiracion = firmaDigital.getGeneradoEn().plus(TOKEN_TTL);
        if (firmaDigital.isExpirado() || Instant.now().isAfter(expiracion)) {
            firmaDigital.setExpirado(true);
            firmaDigitalRepository.save(firmaDigital);
            throw new ResourceNotFoundException("Signature token is invalid or expired");
        }

        Instant firmadoEn = Instant.now();
        firmaDigital.setFirmaDatos(firmaDatos);
        firmaDigital.setDispositivoInfo(dispositivoInfo);
        firmaDigital.setFirmadoEn(firmadoEn);
        firmaDigital.setExpirado(true);

        FirmaDigital savedFirma = firmaDigitalRepository.save(firmaDigital);

        return VerifyFirmaResponse.builder()
                .token(savedFirma.getTokenUnico())
                .estado("FIRMADO")
                .firmadoEn(savedFirma.getFirmadoEn())
                .mensaje("Signature verified successfully")
                .build();
    }

    private UUID parseToken(String token) {
        try {
            return UUID.fromString(token);
        } catch (IllegalArgumentException exception) {
            throw new ResourceNotFoundException("FirmaDigital", token);
        }
    }
}