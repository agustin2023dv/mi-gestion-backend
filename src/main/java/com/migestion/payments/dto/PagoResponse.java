package com.migestion.payments.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PagoResponse {

    private Long id;
    private Long pedidoId;
    private String provider;
    private String transactionId;
    private String paymentMethod;
    private BigDecimal monto;
    private String moneda;
    private String estado;
    private Instant fechaProcesamiento;
}
