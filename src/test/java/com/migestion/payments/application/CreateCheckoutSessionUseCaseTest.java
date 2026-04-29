package com.migestion.payments.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.payments.dto.CreateCheckoutSessionResponse;
import com.migestion.payments.dto.ProviderCheckoutSessionRequest;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCheckoutSessionUseCase")
class CreateCheckoutSessionUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private CreateCheckoutSessionUseCase createCheckoutSessionUseCase;

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should require tenant context before creating checkout session")
    void should_require_tenant_context_before_creating_checkout_session() {
        // Arrange
        TenantContext.clear();

        // Act & Assert
        assertThatThrownBy(() -> createCheckoutSessionUseCase.execute(15L, "https://ok/success", "https://ok/cancel"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Tenant context is required");

        verify(pedidoRepository, never()).findByIdAndTenantId(15L, null);
        verify(paymentGateway, never()).createCheckoutSession(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should reject blank checkout URLs before hitting repository")
    void should_reject_blank_checkout_urls_before_hitting_repository() {
        // Arrange
        TenantContext.setTenantId(9L);

        // Act & Assert
        assertThatThrownBy(() -> createCheckoutSessionUseCase.execute(15L, " ", "https://ok/cancel"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("successUrl and cancelUrl are required");

        verify(pedidoRepository, never()).findByIdAndTenantId(15L, 9L);
        verify(paymentGateway, never()).createCheckoutSession(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should reject null pedidoId before repository lookup")
    void should_reject_null_pedido_id_before_repository_lookup() {
        // Arrange
        TenantContext.setTenantId(9L);

        // Act & Assert
        assertThatThrownBy(() -> createCheckoutSessionUseCase.execute(null, "https://ok/success", "https://ok/cancel"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("pedidoId is required");

        verify(pedidoRepository, never()).findByIdAndTenantId(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(paymentGateway, never()).createCheckoutSession(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should prevent tenant leakage when pedido is not found in current tenant scope")
    void should_prevent_tenant_leakage_when_pedido_is_not_found_in_current_tenant_scope() {
        // Arrange
        TenantContext.setTenantId(31L);
        when(pedidoRepository.findByIdAndTenantId(18L, 31L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> createCheckoutSessionUseCase.execute(18L, "https://ok/success", "https://ok/cancel"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pedido with id 18 not found");

        verify(pedidoRepository).findByIdAndTenantId(18L, 31L);
        verify(paymentGateway, never()).createCheckoutSession(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should reject checkout session when pedido payment is no longer pending")
    void should_reject_checkout_session_when_pedido_payment_is_no_longer_pending() {
        // Arrange
        TenantContext.setTenantId(31L);
        Pedido pedido = org.mockito.Mockito.mock(Pedido.class);

        when(pedido.getEstadoPago()).thenReturn("pagado");

        when(pedidoRepository.findByIdAndTenantId(18L, 31L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        assertThatThrownBy(() -> createCheckoutSessionUseCase.execute(18L, "https://ok/success", "https://ok/cancel"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Checkout session can only be created for orders pending payment");

        verify(paymentGateway, never()).createCheckoutSession(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should map checkout request with tenant scoped pedido data")
    void should_map_checkout_request_with_tenant_scoped_pedido_data() {
        // Arrange
        TenantContext.setTenantId(31L);
        Pedido pedido = org.mockito.Mockito.mock(Pedido.class);
        CreateCheckoutSessionResponse expectedResponse = CreateCheckoutSessionResponse.builder()
                .checkoutUrl("https://checkout/session")
                .sessionId("sess_123")
                .build();

        when(pedido.getEstadoPago()).thenReturn("pendiente");
        when(pedido.getTotal()).thenReturn(new BigDecimal("45.50"));
        when(pedidoRepository.findByIdAndTenantId(18L, 31L)).thenReturn(Optional.of(pedido));
        when(paymentGateway.createCheckoutSession(org.mockito.ArgumentMatchers.any())).thenReturn(expectedResponse);

        ArgumentCaptor<ProviderCheckoutSessionRequest> requestCaptor =
                ArgumentCaptor.forClass(ProviderCheckoutSessionRequest.class);

        // Act
        CreateCheckoutSessionResponse response =
                createCheckoutSessionUseCase.execute(18L, "https://ok/success", "https://ok/cancel");

        // Assert
        assertThat(response.getCheckoutUrl()).isEqualTo("https://checkout/session");
        assertThat(response.getSessionId()).isEqualTo("sess_123");
        verify(paymentGateway).createCheckoutSession(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getTenantId()).isEqualTo(31L);
        assertThat(requestCaptor.getValue().getReferenceType()).isEqualTo("pedido");
        assertThat(requestCaptor.getValue().getReferenceId()).isEqualTo(18L);
        assertThat(requestCaptor.getValue().getAmount()).isEqualByComparingTo("45.50");
        assertThat(requestCaptor.getValue().getCurrency()).isEqualTo("USD");
        assertThat(requestCaptor.getValue().getSuccessUrl()).isEqualTo("https://ok/success");
        assertThat(requestCaptor.getValue().getCancelUrl()).isEqualTo("https://ok/cancel");
    }

    @Test
    @DisplayName("Should create checkout session when pedido payment status is pending in English")
    void should_create_checkout_session_when_pedido_payment_status_is_pending_in_english() {
        // Arrange
        TenantContext.setTenantId(31L);
        Pedido pedido = org.mockito.Mockito.mock(Pedido.class);
        CreateCheckoutSessionResponse expectedResponse = CreateCheckoutSessionResponse.builder()
                .checkoutUrl("https://checkout/session-en")
                .sessionId("sess_english_pending")
                .build();

        when(pedido.getEstadoPago()).thenReturn("pending");
        when(pedido.getTotal()).thenReturn(new BigDecimal("22.00"));
        when(pedidoRepository.findByIdAndTenantId(77L, 31L)).thenReturn(Optional.of(pedido));
        when(paymentGateway.createCheckoutSession(org.mockito.ArgumentMatchers.any())).thenReturn(expectedResponse);

        // Act
        CreateCheckoutSessionResponse response =
                createCheckoutSessionUseCase.execute(77L, "https://ok/success-en", "https://ok/cancel-en");

        // Assert
        assertThat(response).isEqualTo(expectedResponse);
        verify(pedidoRepository).findByIdAndTenantId(77L, 31L);
        verify(paymentGateway).createCheckoutSession(org.mockito.ArgumentMatchers.any());
    }
}