package com.migestion.payments.application;

import com.migestion.payments.domain.Pago;
import com.migestion.payments.domain.PagoRepository;
import com.migestion.payments.dto.RefundRequest;
import com.migestion.payments.dto.RefundResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class RefundPaymentUseCase {

    private final PagoRepository pagoRepository;
    private final PaymentGateway paymentGateway;

    public RefundPaymentUseCase(PagoRepository pagoRepository, PaymentGateway paymentGateway) {
        this.pagoRepository = pagoRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public RefundResponse execute(Long pagoId, BigDecimal amount) {
        return execute(pagoId, amount, null);
    }

    @Transactional
    public RefundResponse execute(Long pagoId, BigDecimal amount, String reason) {
        Long tenantId = resolveTenantId();

        if (pagoId == null) {
            throw new BusinessRuleViolationException("PAGO_ID_REQUIRED", "pagoId is required");
        }

        Pago pago = pagoRepository.findByIdAndTenantId(pagoId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", pagoId));

        if (!StringUtils.hasText(pago.getTransactionId())) {
            throw new BusinessRuleViolationException(
                    "TRANSACTION_ID_REQUIRED",
                    "Payment transaction id is required to process refund"
            );
        }

        BigDecimal refundAmount = amount == null ? pago.getMonto() : amount;
        validateRefundAmount(refundAmount, pago.getMonto());

        RefundResponse refundResponse = paymentGateway.refund(
                RefundRequest.builder()
                        .transactionId(pago.getTransactionId())
                        .amount(refundAmount)
                        .reason(reason)
                        .build()
        );

        if (!refundResponse.isSuccess()) {
            throw new BusinessRuleViolationException("PROVIDER_ERROR", "Payment provider rejected refund request");
        }

        pago.setEstado(isFullRefund(refundAmount, pago.getMonto()) ? "REFUNDED" : "PARTIALLY_REFUNDED");
        pago.setReferenciaReembolso(refundResponse.getRefundId());
        pago.setFechaProcesamiento(Instant.now());
        pago.setRespuestaProvider(Map.of(
                "refundId", refundResponse.getRefundId(),
                "status", refundResponse.getStatus(),
                "refundedAmount", refundResponse.getRefundedAmount()
        ));

        pagoRepository.save(pago);
        return refundResponse;
    }

    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }

    private void validateRefundAmount(BigDecimal refundAmount, BigDecimal paidAmount) {
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("INVALID_REFUND_AMOUNT", "Refund amount must be greater than zero");
        }

        if (paidAmount != null && refundAmount.compareTo(paidAmount) > 0) {
            throw new BusinessRuleViolationException(
                    "REFUND_EXCEEDS_BALANCE",
                    "Refund amount cannot exceed paid amount"
            );
        }
    }

    private boolean isFullRefund(BigDecimal refundAmount, BigDecimal paidAmount) {
        return paidAmount != null && refundAmount.compareTo(paidAmount) == 0;
    }
}
