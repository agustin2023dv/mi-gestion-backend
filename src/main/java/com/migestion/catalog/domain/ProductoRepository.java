package com.migestion.catalog.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByTenantIdAndSku(Long tenantId, String sku);

    Optional<Producto> findByIdAndTenantId(Long id, Long tenantId);
}