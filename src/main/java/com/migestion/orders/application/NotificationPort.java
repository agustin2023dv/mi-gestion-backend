package com.migestion.orders.application;

import java.math.BigDecimal;

/**
 * Port for order notification delivery.
 * Implementations are provided by the infrastructure layer (e.g. WhatsApp, SMS, log).
 */
public interface NotificationPort {

    /**
     * Sends an order-created notification to the customer.
     *
     * @param clienteNombre    customer's first name (or full name for display)
     * @param clienteTelefono  E.164 phone number of the customer
     * @param numeroPedido     human-readable order number
     * @param total            order total amount
     * @param trackingUrl      public URL the customer can use to track the order
     */
    void notifyOrderCreated(String clienteNombre, String clienteTelefono,
                            String numeroPedido, BigDecimal total, String trackingUrl);
}
