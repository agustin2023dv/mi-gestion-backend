package com.migestion.orders.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByIdAndTenantId(Long id, Long tenantId);

    Optional<Pedido> findByIdAndClienteIdAndTenantId(Long id, Long clienteId, Long tenantId);

    Optional<Pedido> findByTrackingToken(String trackingToken);

    Optional<Pedido> findByIdempotencyKey(String idempotencyKey);

    Page<Pedido> findAllByTenantId(Long tenantId, Pageable pageable);

        List<Pedido> findAllByTenantIdAndFechaPedidoGreaterThanEqualAndFechaPedidoLessThan(
            Long tenantId,
            Instant fechaInicio,
            Instant fechaFin);

    boolean existsByIdempotencyKey(String idempotencyKey);

    boolean existsByNumeroPedido(String numeroPedido);
}
