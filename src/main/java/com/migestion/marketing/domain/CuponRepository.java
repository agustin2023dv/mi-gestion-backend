package com.migestion.marketing.domain;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CuponRepository extends JpaRepository<Cupon, Long> {

    Optional<Cupon> findByIdAndTenantId(Long id, Long tenantId);

    Optional<Cupon> findByCodigoAndTenantId(String codigo, Long tenantId);

    Page<Cupon> findAllByTenantId(Long tenantId, Pageable pageable);

    Page<Cupon> findAllByTenantIdAndIsActive(Long tenantId, boolean isActive, Pageable pageable);

    boolean existsByCodigoAndTenantId(String codigo, Long tenantId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Cupon c
            set c.usosActuales = c.usosActuales + 1
            where c.id = :cuponId
              and c.tenantId = :tenantId
              and c.isActive = true
              and (c.fechaInicio is null or c.fechaInicio <= :now)
              and (c.fechaFin is null or c.fechaFin >= :now)
              and (c.usosMaximos is null or c.usosActuales < c.usosMaximos)
            """)
    int incrementUsosActualesIfAvailable(
            @Param("cuponId") Long cuponId,
            @Param("tenantId") Long tenantId,
            @Param("now") Instant now
    );
}
