package com.migestion.payments.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.migestion.payments.domain.Pago;
import com.migestion.payments.domain.PagoRepository;
import com.migestion.payments.dto.RefundResponse;
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
@DisplayName("RefundPaymentUseCase")
class RefundPaymentUseCaseTest {

    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private RefundPaymentUseCase refundPaymentUseCase;

    @AfterEach
    void cleanupTenantContext() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should process full refund for current tenant and update payment state")
    void should_process_full_refund_for_current_tenant_and_update_payment_state() {
        // Arrange
        TenantContext.setTenantId(10L);

        Pago pago = Pago.builder()
                .tenantId(10L)
                .transactionId("pi_123")
                .monto(new BigDecimal("50.00"))
                .estado("PAID")
                .build();

        RefundResponse providerResponse = RefundResponse.builder()
                .success(true)
                .refundId("re_123")
                .refundedAmount(new BigDecimal("50.00"))
                .status("succeeded")
                .build();

        when(pagoRepository.findByIdAndTenantId(99L, 10L)).thenReturn(Optional.of(pago));
        when(paymentGateway.refund(any())).thenReturn(providerResponse);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RefundResponse response = refundPaymentUseCase.execute(99L, null, "duplicate-charge");

        // Assert
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getRefundId()).isEqualTo("re_123");
        verify(pagoRepository).findByIdAndTenantId(99L, 10L);

        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(pagoCaptor.capture());
        assertThat(pagoCaptor.getValue().getEstado()).isEqualTo("REFUNDED");
        assertThat(pagoCaptor.getValue().getReferenciaReembolso()).isEqualTo("re_123");
    }

    @Test
    @DisplayName("Should prevent tenant leakage when payment is not found in current tenant")
    void should_prevent_tenant_leakage_when_payment_is_not_found_in_current_tenant() {
        // Arrange
        TenantContext.setTenantId(10L);
        when(pagoRepository.findByIdAndTenantId(99L, 10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> refundPaymentUseCase.execute(99L, new BigDecimal("10.00"), "error"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pago");

        verify(paymentGateway, never()).refund(any());
    }

    @Test
    @DisplayName("Should throw TENANT_REQUIRED when tenant context is missing")
    void should_throw_tenant_required_when_tenant_context_is_missing() {
        // Arrange
        TenantContext.clear();

        // Act & Assert
        assertThatThrownBy(() -> refundPaymentUseCase.execute(99L, new BigDecimal("10.00"), "error"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Tenant context is required");

        verify(pagoRepository, never()).findByIdAndTenantId(99L, 10L);
    }
}
