package com.migestion.logistics.infrastructure;

import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.orders.application.DeliveryCreationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderDeliveryCreationAdapter implements DeliveryCreationPort {

    private static final String PENDING_DELIVERY_STATE = "PENDIENTE";

    private final EntregaRepository entregaRepository;

    public OrderDeliveryCreationAdapter(EntregaRepository entregaRepository) {
        this.entregaRepository = entregaRepository;
    }

    @Override
    @Transactional
    public void createPendingDelivery(Long tenantId, Long pedidoId) {
        boolean alreadyExists = entregaRepository.existsByPedidoIdAndTenantId(pedidoId, tenantId);
        if (alreadyExists) {
            return;
        }

        Entrega entrega = Entrega.builder()
                .tenantId(tenantId)
                .pedidoId(pedidoId)
                .estado(PENDING_DELIVERY_STATE)
                .build();

        entregaRepository.save(entrega);
    }
}