package com.migestion.orders.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {

    Optional<EstadoPedido> findByCodigo(String codigo);
}
