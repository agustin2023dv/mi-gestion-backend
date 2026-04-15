package com.migestion.marketing.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaProductoRepository extends JpaRepository<ResenaProducto, Long> {

    Optional<ResenaProducto> findByIdAndTenantId(Long id, Long tenantId);

    Page<ResenaProducto> findByProductoIdAndIsPublishedTrue(Long productoId, Pageable pageable);

    Page<ResenaProducto> findByTenantIdAndRequiereModeracionTrue(Long tenantId, Pageable pageable);

    Optional<ResenaProducto> findByClienteIdAndProductoIdAndPedidoId(Long clienteId, Long productoId, Long pedidoId);
}
