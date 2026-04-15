package com.migestion.orders.application;

import java.math.BigDecimal;

public interface DeliveryCostPort {

    BigDecimal calculateDeliveryCost(Long tenantId, Long direccionEntregaId);
}