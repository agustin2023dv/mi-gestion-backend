package com.migestion.orders.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByIdAndTenantId(Long id, Long tenantId);

    List<Pedido> findAllByTenantId(Long tenantId);

    boolean existsByIdempotencyKey(String idempotencyKey);

    boolean existsByNumeroPedido(String numeroPedido);
}
