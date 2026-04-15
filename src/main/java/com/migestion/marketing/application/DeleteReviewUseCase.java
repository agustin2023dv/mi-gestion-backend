package com.migestion.marketing.application;

import com.migestion.marketing.domain.ResenaProducto;
import com.migestion.marketing.domain.ResenaProductoRepository;
import com.migestion.marketing.domain.ResenaTenant;
import com.migestion.marketing.domain.ResenaTenantRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteReviewUseCase {

    private final ResenaProductoRepository resenaProductoRepository;
    private final ResenaTenantRepository resenaTenantRepository;

    public void deleteProductReview(Long reviewId) {
        Long tenantId = requireTenantId();

        ResenaProducto resena = resenaProductoRepository.findByIdAndTenantId(reviewId, tenantId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                    "RESENA_NO_ENCONTRADA",
                    "La reseña no existe o no pertenece a este tenant"));

        resena.setDeletedAt(Instant.now());
        resenaProductoRepository.save(resena);
    }

    public void deleteTenantReview(Long reviewId) {
        Long tenantId = requireTenantId();

        ResenaTenant resena = resenaTenantRepository.findByIdAndTenantId(reviewId, tenantId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                    "RESENA_NO_ENCONTRADA",
                    "La reseña no existe o no pertenece a este tenant"));

        resena.setDeletedAt(Instant.now());
        resenaTenantRepository.save(resena);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                "TENANT_NO_ENCONTRADO",
                "No se encuentra información del tenant");
        }
        return tenantId;
    }
}
