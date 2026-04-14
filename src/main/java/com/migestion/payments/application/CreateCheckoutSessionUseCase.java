package com.migestion.payments.application;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.payments.dto.CreateCheckoutSessionRequest;
import com.migestion.payments.dto.CreateCheckoutSessionResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CreateCheckoutSessionUseCase {

    private final PedidoRepository pedidoRepository;
    private final PaymentGateway paymentGateway;

    public CreateCheckoutSessionUseCase(PedidoRepository pedidoRepository, PaymentGateway paymentGateway) {
        this.pedidoRepository = pedidoRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public CreateCheckoutSessionResponse execute(Long pedidoId, String successUrl, String cancelUrl) {
        Long tenantId = resolveTenantId();

        if (pedidoId == null) {
            throw new BusinessRuleViolationException("PEDIDO_ID_REQUIRED", "pedidoId is required");
        }

        if (!StringUtils.hasText(successUrl) || !StringUtils.hasText(cancelUrl)) {
            throw new BusinessRuleViolationException(
                    "CHECKOUT_URLS_REQUIRED",
                    "successUrl and cancelUrl are required"
            );
        }

        Pedido pedido = pedidoRepository.findByIdAndTenantId(pedidoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", pedidoId));

        if (!isPendingPayment(pedido.getEstadoPago())) {
            throw new BusinessRuleViolationException(
                    "PAYMENT_ALREADY_COMPLETED",
                    "Checkout session can only be created for orders pending payment"
            );
        }

        CreateCheckoutSessionRequest request = CreateCheckoutSessionRequest.builder()
                .tenantId(tenantId)
                .referenceType("pedido")
                .referenceId(pedidoId)
                .amount(pedido.getTotal())
                .currency("USD")
                .successUrl(successUrl)
                .cancelUrl(cancelUrl)
                .build();

        return paymentGateway.createCheckoutSession(request);
    }

    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }

    private boolean isPendingPayment(String estadoPago) {
        return StringUtils.hasText(estadoPago)
                && ("pendiente".equalsIgnoreCase(estadoPago) || "pending".equalsIgnoreCase(estadoPago));
    }
}
