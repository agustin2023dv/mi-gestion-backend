package com.migestion.payments.application;

import com.migestion.payments.dto.CreateCheckoutSessionRequest;
import com.migestion.payments.dto.CreateCheckoutSessionResponse;
import com.migestion.payments.dto.HandleWebhookResponse;
import com.migestion.payments.dto.RefundRequest;
import com.migestion.payments.dto.RefundResponse;

public interface PaymentGateway {

    CreateCheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request);

    HandleWebhookResponse handleWebhook(String payload, String signature);

    RefundResponse refund(RefundRequest request);
}