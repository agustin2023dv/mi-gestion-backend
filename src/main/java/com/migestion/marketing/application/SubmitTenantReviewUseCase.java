package com.migestion.marketing.application;

import com.migestion.marketing.domain.ResenaTenant;
import com.migestion.marketing.domain.ResenaTenantRepository;
import com.migestion.marketing.dto.CreateTenantReviewRequest;
import com.migestion.marketing.dto.ReviewResponse;
import com.migestion.marketing.infrastructure.mapper.ReviewMapper;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitTenantReviewUseCase {

    private final ResenaTenantRepository resenaTenantRepository;
    private final PedidoRepository pedidoRepository;
    private final ReviewMapper reviewMapper;

    public ReviewResponse execute(Long clienteId, CreateTenantReviewRequest request) {
        Long tenantId = requireTenantId();

        // Verify at least one of pedidoId or turnoId is provided
        if (request.pedidoId() == null && request.turnoId() == null) {
            throw new BusinessRuleViolationException(
                "VALIDACION_REQUERIDA",
                "Se requiere un pedido o turno para verificar la reseña");
        }

        // If pedidoId is provided, verify the order exists and belongs to the client
        if (request.pedidoId() != null) {
            Pedido pedido = pedidoRepository.findByIdAndClienteIdAndTenantId(
                    request.pedidoId(), clienteId, tenantId)
                    .orElseThrow(() -> new BusinessRuleViolationException(
                        "PEDIDO_NO_ENCONTRADO",
                        "El pedido no existe o no pertenece al cliente"));

            // Verify the order is in ENTREGADO status
            if (!"ENTREGADO".equals(pedido.getEstado().getNombre())) {
                throw new BusinessRuleViolationException(
                    "PEDIDO_NO_ENTREGADO",
                    "Solo se pueden calificar después de completar una transacción");
            }

            // Check if client already reviewed this tenant in this order
            resenaTenantRepository.findByClienteIdAndTenantIdAndPedidoId(
                    clienteId, tenantId, request.pedidoId())
                    .ifPresent(existing -> {
                        throw new BusinessRuleViolationException(
                            "RESENA_DUPLICADA",
                            "Ya existe una reseña de este cliente para este tenant en este pedido");
                    });
        }

        // Create the review
        ResenaTenant resena = reviewMapper.toResenaTenantEntity(request);
        resena.setTenantId(tenantId);
        resena.setClienteId(clienteId);
        resena.setEsVerificada(true);
        resena.setRequiereModeracion(true); // Default to requiring moderation
        resena.setPublished(false);

        ResenaTenant saved = resenaTenantRepository.save(resena);
        return reviewMapper.toReviewResponse(saved);
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

