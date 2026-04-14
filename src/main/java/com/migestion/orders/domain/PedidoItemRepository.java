package com.migestion.orders.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    List<PedidoItem> findAllByPedidoId(Long pedidoId);
}
