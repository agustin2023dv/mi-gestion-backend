package com.migestion.orders.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoEstadoHistorialRepository extends JpaRepository<PedidoEstadoHistorial, Long> {

    List<PedidoEstadoHistorial> findAllByPedidoIdOrderByFechaCambioAsc(Long pedidoId);
}
