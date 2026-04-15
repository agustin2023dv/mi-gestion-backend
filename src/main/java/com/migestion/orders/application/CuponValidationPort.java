package com.migestion.orders.application;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public interface CuponValidationPort {

    CuponValidationResult validateCoupon(String codigo, BigDecimal montoPedido);

    void registerCouponUsage(Long pedidoId, Long cuponId, BigDecimal montoDescuento);

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class CuponValidationResult {
        private final Long cuponId;
        private final String codigo;
        private final BigDecimal montoDescuento;
    }
}