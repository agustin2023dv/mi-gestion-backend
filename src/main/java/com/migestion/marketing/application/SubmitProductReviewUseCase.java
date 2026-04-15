package com.migestion.marketing.application;

import com.migestion.marketing.domain.ResenaProducto;
import com.migestion.marketing.domain.ResenaProductoRepository;
import com.migestion.marketing.dto.CreateReviewRequest;
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
public class SubmitProductReviewUseCase {

    private final ResenaProductoRepository resenaProductoRepository;
    private final PedidoRepository pedidoRepository;
    private final ReviewMapper reviewMapper;

    public ReviewResponse execute(Long clienteId, CreateReviewRequest request) {
        Long tenantId = requireTenantId();

        // Verify the order exists and belongs to the client and tenant
        Pedido pedido = pedidoRepository.findByIdAndClienteIdAndTenantId(
                request.pedidoId(), clienteId, tenantId)
                .orElseThrow(() -> new BusinessRuleViolationException(
                    "PEDIDO_NO_ENCONTRADO",
                    "El pedido no existe o no pertenece al cliente"));

        // Verify the order is in ENTREGADO status
        if (!"ENTREGADO".equals(pedido.getEstado().getNombre())) {
            throw new BusinessRuleViolationException(
                "PEDIDO_NO_ENTREGADO",
                "Solo se pueden calificar productos de pedidos entregados");
        }

        // Check if client already reviewed this product in this order
        resenaProductoRepository.findByClienteIdAndProductoIdAndPedidoId(
                clienteId, request.productoId(), request.pedidoId())
                .ifPresent(existing -> {
                    throw new BusinessRuleViolationException(
                        "RESENA_DUPLICADA",
                        "Ya existe una reseña de este cliente para este producto en este pedido");
                });

        // Create the review
        ResenaProducto resena = reviewMapper.toResenaProductoEntity(request);
        resena.setTenantId(tenantId);
        resena.setClienteId(clienteId);
        resena.setEsVerificada(true);
        resena.setRequiereModeracion(true); // Default to requiring moderation
        resena.setPublished(false);

        ResenaProducto saved = resenaProductoRepository.save(resena);
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
