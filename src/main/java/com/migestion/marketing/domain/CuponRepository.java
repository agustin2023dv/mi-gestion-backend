package com.migestion.marketing.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuponRepository extends JpaRepository<Cupon, Long> {

    Optional<Cupon> findByIdAndTenantId(Long id, Long tenantId);

    Optional<Cupon> findByCodigoAndTenantId(String codigo, Long tenantId);

    Page<Cupon> findAllByTenantId(Long tenantId, Pageable pageable);

    Page<Cupon> findAllByTenantIdAndIsActive(Long tenantId, boolean isActive, Pageable pageable);

    boolean existsByCodigoAndTenantId(String codigo, Long tenantId);
}
