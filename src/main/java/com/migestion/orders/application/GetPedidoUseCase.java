package com.migestion.orders.application;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.dto.PedidoResponse;
import com.migestion.orders.infrastructure.mapper.PedidoMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPedidoUseCase {

    private static final String CLIENTE_ROLE = "CLIENTE";

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final PedidoMapper pedidoMapper;

    public GetPedidoUseCase(
            PedidoRepository pedidoRepository,
            PedidoItemRepository pedidoItemRepository,
            PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional(readOnly = true)
    public PedidoResponse execute(Long id, Long tenantId, String callerRole, Long callerClienteId) {
        Pedido pedido;

        if (CLIENTE_ROLE.equals(callerRole)) {
            pedido = pedidoRepository.findByIdAndClienteIdAndTenantId(id, callerClienteId, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
        } else {
            pedido = pedidoRepository.findByIdAndTenantId(id, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
        }

        List<PedidoItem> items = pedidoItemRepository.findAllByPedidoId(pedido.getId());
        return pedidoMapper.toResponse(pedido, items);
    }
}
