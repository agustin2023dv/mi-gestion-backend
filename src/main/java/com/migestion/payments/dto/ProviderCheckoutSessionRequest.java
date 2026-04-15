package com.migestion.payments.dto;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProviderCheckoutSessionRequest {

    private Long tenantId;
    private String referenceType;
    private Long referenceId;
    private BigDecimal amount;
    private String currency;
    private String successUrl;
    private String cancelUrl;
}
