package com.migestion.orders.application;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoEstadoHistorial;
import com.migestion.orders.domain.PedidoEstadoHistorialRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.dto.PedidoTrackingResponse;
import com.migestion.orders.infrastructure.mapper.PedidoMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrackPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PedidoEstadoHistorialRepository historialRepository;
    private final PedidoMapper pedidoMapper;

    public TrackPedidoUseCase(PedidoRepository pedidoRepository,
                              PedidoEstadoHistorialRepository historialRepository,
                              PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.historialRepository = historialRepository;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional(readOnly = true)
    public PedidoTrackingResponse execute(String trackingToken) {
        Pedido pedido = pedidoRepository.findByTrackingToken(trackingToken)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", trackingToken));

        List<PedidoEstadoHistorial> historial =
                historialRepository.findAllByPedidoIdOrderByFechaCambioAsc(pedido.getId());

        return pedidoMapper.toTrackingResponse(pedido, historial);
    }
}
