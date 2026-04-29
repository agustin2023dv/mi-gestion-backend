package com.migestion.payments.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.domain.event.OrderPaidEvent;
import com.migestion.payments.domain.Pago;
import com.migestion.payments.domain.PagoRepository;
import com.migestion.payments.dto.HandleWebhookResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("HandleStripeWebhookUseCase")
class HandleStripeWebhookUseCaseTest {

    @Mock
    private PaymentGateway paymentGateway;
    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

        private HandleStripeWebhookUseCase handleStripeWebhookUseCase;

        @BeforeEach
        void setup() {
                handleStripeWebhookUseCase = new HandleStripeWebhookUseCase(
                                paymentGateway,
                                pagoRepository,
                                pedidoRepository,
                                applicationEventPublisher,
                                new ObjectMapper()
                );
        }

    @Test
    @DisplayName("Should process checkout completed webhook and publish paid event")
    void should_process_checkout_completed_webhook_and_publish_paid_event() {
        // Arrange
        String payload = """
                {
                  "type": "checkout.session.completed",
                  "data": {
                    "object": {
                      "id": "cs_test_1",
                      "client_reference_id": "pedido:55",
                      "payment_intent": "pi_test_1",
                      "amount_total": 2599,
                      "currency": "usd",
                      "payment_method_types": ["card"]
                    }
                  }
                }
                """;

        HandleWebhookResponse gatewayResponse = HandleWebhookResponse.builder()
                .received(true)
                .eventId("evt_1")
                .build();

        Pedido pedido = Pedido.builder()
                .tenantId(7L)
                .total(new BigDecimal("25.99"))
                .build();

        when(paymentGateway.handleWebhook(payload, "signature-1")).thenReturn(gatewayResponse);
        when(pedidoRepository.findById(55L)).thenReturn(Optional.of(pedido));
        when(pagoRepository.findByTransactionIdAndTenantId("pi_test_1", 7L)).thenReturn(Optional.empty());
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        HandleWebhookResponse response = handleStripeWebhookUseCase.execute(payload, "signature-1");

        // Assert
        assertThat(response.isReceived()).isTrue();
        assertThat(response.getEventId()).isEqualTo("evt_1");
        verify(pagoRepository).findByTransactionIdAndTenantId("pi_test_1", 7L);

        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(pagoCaptor.capture());
        assertThat(pagoCaptor.getValue().getTenantId()).isEqualTo(7L);
        assertThat(pagoCaptor.getValue().getEstado()).isEqualTo("PAID");

        verify(applicationEventPublisher).publishEvent(any(OrderPaidEvent.class));
    }

    @Test
    @DisplayName("Should return gateway response without side effects for non-checkout event")
    void should_return_gateway_response_without_side_effects_for_non_checkout_event() {
        // Arrange
        String payload = """
                {
                  "type": "payment_intent.created",
                  "data": { "object": { "id": "pi_test_2" } }
                }
                """;

        HandleWebhookResponse gatewayResponse = HandleWebhookResponse.builder()
                .received(true)
                .eventId("evt_2")
                .build();

        when(paymentGateway.handleWebhook(payload, "signature-2")).thenReturn(gatewayResponse);

        // Act
        HandleWebhookResponse response = handleStripeWebhookUseCase.execute(payload, "signature-2");

        // Assert
        assertThat(response.getEventId()).isEqualTo("evt_2");
        verify(pedidoRepository, never()).findById(55L);
        verify(pagoRepository, never()).save(any(Pago.class));
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should throw INVALID_SIGNATURE when gateway rejects signature")
    void should_throw_invalid_signature_when_gateway_rejects_signature() {
        // Arrange
        String payload = """
                {
                  "type": "checkout.session.completed",
                  "data": { "object": { "client_reference_id": "pedido:55" } }
                }
                """;

        when(paymentGateway.handleWebhook(payload, "invalid-signature"))
                .thenReturn(HandleWebhookResponse.builder().received(false).eventId("evt_3").build());

        // Act & Assert
        assertThatThrownBy(() -> handleStripeWebhookUseCase.execute(payload, "invalid-signature"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Stripe signature verification failed");
    }
}
