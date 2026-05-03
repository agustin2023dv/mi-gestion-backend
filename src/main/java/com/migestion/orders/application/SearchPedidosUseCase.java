package com.migestion.orders.application;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.dto.PedidoResponse;
import com.migestion.orders.infrastructure.mapper.PedidoMapper;
import com.migestion.shared.dto.PageResponse;
import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchPedidosUseCase {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    public SearchPedidosUseCase(PedidoRepository pedidoRepository, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<PedidoResponse> execute(Long tenantId, Pageable pageable) {
        Page<Pedido> page = pedidoRepository.findAllByTenantId(tenantId, pageable);

        return PageResponse.<PedidoResponse>builder()
                .content(page.getContent().stream()
                        .map(p -> pedidoMapper.toResponse(p, Collections.emptyList()))
                        .toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
