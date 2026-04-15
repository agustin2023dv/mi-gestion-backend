package com.migestion.marketing.application;

import com.migestion.marketing.domain.ResenaProducto;
import com.migestion.marketing.domain.ResenaProductoRepository;
import com.migestion.marketing.domain.ResenaTenant;
import com.migestion.marketing.domain.ResenaTenantRepository;
import com.migestion.marketing.dto.ModerateReviewRequest;
import com.migestion.marketing.dto.ReviewResponse;
import com.migestion.marketing.infrastructure.mapper.ReviewMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ModerateReviewUseCase {

    private final ResenaProductoRepository resenaProductoRepository;
    private final ResenaTenantRepository resenaTenantRepository;
    private final ReviewMapper reviewMapper;

    public ReviewResponse executeProductReview(Long reviewId, ModerateReviewRequest request) {
        Long tenantId = requireTenantId();

        ResenaProducto resena = resenaProductoRepository.findByIdAndTenantId(reviewId, tenantId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                    "RESENA_NO_ENCONTRADA",
                    "La reseña no existe o no pertenece a este tenant"));

        if ("APPROVE".equals(request.accion())) {
            resena.setPublished(true);
            resena.setRequiereModeracion(false);
            resena.setMotivoRechazo(null);
        } else if ("REJECT".equals(request.accion())) {
            resena.setPublished(false);
            resena.setRequiereModeracion(false);
            resena.setMotivoRechazo(request.motivoRechazo());
        } else {
            throw new BusinessRuleViolationException(
                "ACCION_INVALIDA",
                "La acción debe ser APPROVE o REJECT");
        }

        ResenaProducto updated = resenaProductoRepository.save(resena);
        return reviewMapper.toReviewResponse(updated);
    }

    public ReviewResponse executeTenantReview(Long reviewId, ModerateReviewRequest request) {
        Long tenantId = requireTenantId();

        ResenaTenant resena = resenaTenantRepository.findByIdAndTenantId(reviewId, tenantId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                    "RESENA_NO_ENCONTRADA",
                    "La reseña no existe o no pertenece a este tenant"));

        if ("APPROVE".equals(request.accion())) {
            resena.setPublished(true);
            resena.setRequiereModeracion(false);
            resena.setMotivoRechazo(null);
        } else if ("REJECT".equals(request.accion())) {
            resena.setPublished(false);
            resena.setRequiereModeracion(false);
            resena.setMotivoRechazo(request.motivoRechazo());
        } else {
            throw new BusinessRuleViolationException(
                "ACCION_INVALIDA",
                "La acción debe ser APPROVE o REJECT");
        }

        ResenaTenant updated = resenaTenantRepository.save(resena);
        return reviewMapper.toReviewResponse(updated);
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
