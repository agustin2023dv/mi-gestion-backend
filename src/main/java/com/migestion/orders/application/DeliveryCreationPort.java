package com.migestion.orders.application;

public interface DeliveryCreationPort {

    void createPendingDelivery(Long tenantId, Long pedidoId);
}