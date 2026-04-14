package com.migestion.orders.infrastructure;

import com.migestion.orders.application.NotificationPort;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stub adapter that simulates WhatsApp notification by logging the message.
 * Replace this bean with a real WhatsApp Business API adapter when available.
 */
@Component
public class LogNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LogNotificationAdapter.class);

    @Override
    public void notifyOrderCreated(String clienteNombre, String clienteTelefono,
                                   String numeroPedido, BigDecimal total, String trackingUrl) {
        String message = String.format(
                "[WhatsApp simulation] To: %s | "
                + "Hola %s, tu pedido #%s ha sido recibido correctamente. "
                + "Total: $%s. "
                + "Sigue el estado de tu pedido en: %s",
                clienteTelefono,
                clienteNombre,
                numeroPedido,
                total,
                trackingUrl
        );
        log.info(message);
    }
}
