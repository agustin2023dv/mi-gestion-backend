package com.migestion.payments.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.domain.event.OrderPaidEvent;
import com.migestion.payments.domain.Pago;
import com.migestion.payments.domain.PagoRepository;
import com.migestion.payments.dto.HandleWebhookResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class HandleStripeWebhookUseCase {

    private static final String CHECKOUT_COMPLETED_EVENT = "checkout.session.completed";

    private final PaymentGateway paymentGateway;
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    public HandleStripeWebhookUseCase(
            PaymentGateway paymentGateway,
            PagoRepository pagoRepository,
            PedidoRepository pedidoRepository,
            ApplicationEventPublisher applicationEventPublisher,
            ObjectMapper objectMapper
    ) {
        this.paymentGateway = paymentGateway;
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public HandleWebhookResponse execute(String payload, String signature) {
        if (!StringUtils.hasText(payload) || !StringUtils.hasText(signature)) {
            throw new BusinessRuleViolationException("INVALID_SIGNATURE", "Webhook payload and signature are required");
        }

        HandleWebhookResponse gatewayResponse = paymentGateway.handleWebhook(payload, signature);
        if (!gatewayResponse.isReceived()) {
            throw new BusinessRuleViolationException("INVALID_SIGNATURE", "Stripe signature verification failed");
        }

        JsonNode root = readPayload(payload);
        String eventType = root.path("type").asText("");
        if (!CHECKOUT_COMPLETED_EVENT.equals(eventType)) {
            return gatewayResponse;
        }

        JsonNode eventObject = root.path("data").path("object");
        Long pedidoId = resolvePedidoId(eventObject);
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", pedidoId));

        String transactionId = resolveTransactionId(eventObject);
        BigDecimal paidAmount = resolvePaidAmount(eventObject, pedido.getTotal());
        String currency = resolveCurrency(eventObject);
        String paymentMethod = resolvePaymentMethod(eventObject);

        Pago pago = pagoRepository.findByTransactionIdAndTenantId(transactionId, pedido.getTenantId())
                .orElseGet(() -> Pago.builder()
                        .tenantId(pedido.getTenantId())
                        .pedidoId(pedido.getId())
                        .provider("stripe")
                        .transactionId(transactionId)
                        .tipoTransaccion("pago")
                        .build());

        pago.setPaymentMethod(paymentMethod);
        pago.setMonto(paidAmount);
        pago.setMoneda(currency);
        pago.setEstado("PAID");
        pago.setFechaProcesamiento(Instant.now());
        pago.setDetallePago(convertToMap(eventObject));
        pago.setRespuestaProvider(convertToMap(root));

        Pago savedPago = pagoRepository.save(pago);

        applicationEventPublisher.publishEvent(
                OrderPaidEvent.builder()
                        .pedidoId(pedido.getId())
                        .tenantId(pedido.getTenantId())
                        .monto(savedPago.getMonto())
                        .transactionId(savedPago.getTransactionId())
                        .build()
        );

        return HandleWebhookResponse.builder()
                .received(true)
                .eventId(gatewayResponse.getEventId())
                .build();
    }

    private JsonNode readPayload(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (Exception ex) {
            throw new BusinessRuleViolationException("INVALID_WEBHOOK_PAYLOAD", "Malformed Stripe webhook payload");
        }
    }

    private Long resolvePedidoId(JsonNode eventObject) {
        String clientReference = eventObject.path("client_reference_id").asText(null);
        if (StringUtils.hasText(clientReference) && clientReference.startsWith("pedido:")) {
            return parseLong(clientReference.substring("pedido:".length()), "INVALID_REFERENCE_ID");
        }

        String metadataPedidoId = eventObject.path("metadata").path("pedidoId").asText(null);
        if (StringUtils.hasText(metadataPedidoId)) {
            return parseLong(metadataPedidoId, "INVALID_REFERENCE_ID");
        }

        throw new BusinessRuleViolationException(
                "PEDIDO_REFERENCE_REQUIRED",
                "Stripe event must include pedido reference"
        );
    }

    private String resolveTransactionId(JsonNode eventObject) {
        String paymentIntent = eventObject.path("payment_intent").asText(null);
        if (StringUtils.hasText(paymentIntent)) {
            return paymentIntent;
        }

        String sessionId = eventObject.path("id").asText(null);
        if (StringUtils.hasText(sessionId)) {
            return sessionId;
        }

        throw new BusinessRuleViolationException("TRANSACTION_ID_REQUIRED", "Transaction id was not found in event");
    }

    private BigDecimal resolvePaidAmount(JsonNode eventObject, BigDecimal defaultAmount) {
        JsonNode amountTotalNode = eventObject.path("amount_total");
        if (amountTotalNode.isNumber()) {
            return BigDecimal.valueOf(amountTotalNode.asLong()).movePointLeft(2);
        }
        return defaultAmount;
    }

    private String resolveCurrency(JsonNode eventObject) {
        String currency = eventObject.path("currency").asText("USD");
        return currency.toUpperCase();
    }

    private String resolvePaymentMethod(JsonNode eventObject) {
        JsonNode paymentMethodsNode = eventObject.path("payment_method_types");
        if (paymentMethodsNode.isArray() && !paymentMethodsNode.isEmpty()) {
            return paymentMethodsNode.get(0).asText("card");
        }
        return "card";
    }

    private Long parseLong(String value, String errorCode) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new BusinessRuleViolationException(errorCode, "Invalid numeric value: " + value);
        }
    }

    private Map<String, Object> convertToMap(JsonNode node) {
        return objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {
        });
    }
}
