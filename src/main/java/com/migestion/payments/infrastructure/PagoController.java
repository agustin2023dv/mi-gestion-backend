package com.migestion.payments.infrastructure;

import com.migestion.payments.application.CreateCheckoutSessionUseCase;
import com.migestion.payments.application.HandleStripeWebhookUseCase;
import com.migestion.payments.application.RefundPaymentUseCase;
import com.migestion.payments.domain.Pago;
import com.migestion.payments.domain.PagoRepository;
import com.migestion.payments.dto.CheckoutSessionResponse;
import com.migestion.payments.dto.CreateCheckoutSessionRequest;
import com.migestion.payments.dto.CreateCheckoutSessionResponse;
import com.migestion.payments.dto.HandleWebhookResponse;
import com.migestion.payments.dto.PagoResponse;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private static final String CLIENTE_ROLE = "ROLE_CLIENTE";

    private final CreateCheckoutSessionUseCase createCheckoutSessionUseCase;
    private final HandleStripeWebhookUseCase handleStripeWebhookUseCase;
    private final RefundPaymentUseCase refundPaymentUseCase;
    private final PagoRepository pagoRepository;
    private final String defaultSuccessUrl;
    private final String defaultCancelUrl;

    public PagoController(
            CreateCheckoutSessionUseCase createCheckoutSessionUseCase,
            HandleStripeWebhookUseCase handleStripeWebhookUseCase,
            RefundPaymentUseCase refundPaymentUseCase,
            PagoRepository pagoRepository,
            @Value("${payments.checkout.success-url:https://example.com/pagos/success}") String defaultSuccessUrl,
            @Value("${payments.checkout.cancel-url:https://example.com/pagos/cancel}") String defaultCancelUrl) {
        this.createCheckoutSessionUseCase = createCheckoutSessionUseCase;
        this.handleStripeWebhookUseCase = handleStripeWebhookUseCase;
        this.refundPaymentUseCase = refundPaymentUseCase;
        this.pagoRepository = pagoRepository;
        this.defaultSuccessUrl = defaultSuccessUrl;
        this.defaultCancelUrl = defaultCancelUrl;
    }

    @PostMapping("/sesiones-checkout")
    public ResponseEntity<ApiResponse<CheckoutSessionResponse>> createCheckoutSession(
            @Valid @RequestBody CreateCheckoutSessionRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication authentication) {

        requireAuthenticatedRole(authentication, CLIENTE_ROLE, "Only CLIENTE users can create checkout sessions");

        CreateCheckoutSessionResponse response = createCheckoutSessionUseCase.execute(
                request.getPedidoId(),
                defaultSuccessUrl,
                defaultCancelUrl
        );

        CheckoutSessionResponse body = CheckoutSessionResponse.builder()
                .checkoutUrl(response.getCheckoutUrl())
                .sessionId(response.getSessionId())
                .build();

        return ResponseEntity.ok(ApiResponse.success(body));
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<HandleWebhookResponse>> webhook(
            @RequestHeader("Stripe-Signature") String stripeSignature,
            @RequestBody String payload) {

        HandleWebhookResponse response = handleStripeWebhookUseCase.execute(payload, stripeSignature);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{pagoId}/reembolsos")
    public ResponseEntity<ApiResponse<PagoResponse>> reembolsar(
            @PathVariable Long pagoId,
            @RequestBody(required = false) ReembolsarPagoRequest request,
            Authentication authentication) {

        requireTenantAdmin(authentication);

        BigDecimal monto = request != null ? request.getMonto() : null;
        String motivo = request != null ? request.getMotivo() : null;

        refundPaymentUseCase.execute(pagoId, monto, motivo);

        Long tenantId = TenantContext.getTenantId();
        Pago pago = pagoRepository.findByIdAndTenantId(pagoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", pagoId));

        PagoResponse response = PagoResponse.builder()
                .id(pago.getId())
                .pedidoId(pago.getPedidoId())
                .provider(pago.getProvider())
                .transactionId(pago.getTransactionId())
                .paymentMethod(pago.getPaymentMethod())
                .monto(pago.getMonto())
                .moneda(pago.getMoneda())
                .estado(pago.getEstado())
                .fechaProcesamiento(pago.getFechaProcesamiento() != null ? pago.getFechaProcesamiento() : Instant.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private void requireAuthenticatedRole(Authentication authentication, String role, String message) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException(message);
        }

        boolean hasRole = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .anyMatch(role::equals);

        if (!hasRole) {
            throw new AccessDeniedException(message);
        }
    }

    private void requireTenantAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Tenant admin authentication is required");
        }

        Collection<String> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        boolean isTenantAdmin = authorities.contains("ROLE_TENANT_ADMIN")
                || authorities.contains("ROLE_TENANTADMIN")
                || authorities.contains("ROLE_ADMIN");

        if (!isTenantAdmin) {
            throw new AccessDeniedException("Only TenantAdmin can refund payments");
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReembolsarPagoRequest {

        private BigDecimal monto;
        private String motivo;
    }
}
