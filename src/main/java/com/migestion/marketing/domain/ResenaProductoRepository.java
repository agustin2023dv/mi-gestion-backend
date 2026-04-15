package com.migestion.marketing.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaProductoRepository extends JpaRepository<ResenaProducto, Long> {

    Optional<ResenaProducto> findByIdAndTenantId(Long id, Long tenantId);
}
