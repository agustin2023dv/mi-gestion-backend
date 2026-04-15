package com.migestion.payments.infrastructure;

import com.migestion.payments.application.PaymentGateway;
import com.migestion.payments.dto.CreateCheckoutSessionResponse;
import com.migestion.payments.dto.HandleWebhookResponse;
import com.migestion.payments.dto.ProviderCheckoutSessionRequest;
import com.migestion.payments.dto.RefundRequest;
import com.migestion.payments.dto.RefundResponse;
import com.stripe.Stripe;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentGatewayAdapter implements PaymentGateway {

    private final String stripeWebhookSecret;

    public StripePaymentGatewayAdapter(
            @Value("${stripe.api-key}") String stripeApiKey,
            @Value("${stripe.webhook-secret}") String stripeWebhookSecret) {
        this.stripeWebhookSecret = stripeWebhookSecret;
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public CreateCheckoutSessionResponse createCheckoutSession(ProviderCheckoutSessionRequest request) {
        String sessionId = "cs_mock_" + UUID.randomUUID();
        return CreateCheckoutSessionResponse.builder()
                .sessionId(sessionId)
                .checkoutUrl("https://checkout.stripe.com/c/pay/" + sessionId)
                .build();
    }

    @Override
    public HandleWebhookResponse handleWebhook(String payload, String signature) {
        return HandleWebhookResponse.builder()
                .received(signature != null && !signature.isBlank() && stripeWebhookSecret != null)
                .eventId("evt_mock_" + UUID.randomUUID())
                .build();
    }

    @Override
    public RefundResponse refund(RefundRequest request) {
        return RefundResponse.builder()
                .success(true)
                .refundId("re_mock_" + UUID.randomUUID())
                .refundedAmount(request.getAmount())
                .status("PENDING")
                .build();
    }
}