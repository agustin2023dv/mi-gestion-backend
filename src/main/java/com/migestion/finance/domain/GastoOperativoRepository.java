package com.migestion.finance.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Long> {

    Optional<GastoOperativo> findByIdAndTenantId(Long id, Long tenantId);

    List<GastoOperativo> findAllByTenantId(Long tenantId);

    @Query("""
            SELECT g FROM GastoOperativo g
            WHERE g.tenantId = :tenantId
              AND (:fechaDesde IS NULL OR g.fechaRegistro >= :fechaDesde)
              AND (:fechaHasta IS NULL OR g.fechaRegistro <= :fechaHasta)
              AND (:categoriaGastoId IS NULL OR g.categoriaGastoId = :categoriaGastoId)
              AND (:centroCostoId IS NULL OR g.centroCostoId = :centroCostoId)
              AND (:esProrrateable IS NULL OR g.esProrrateable = :esProrrateable)
            """)
    Page<GastoOperativo> findAllWithFilters(
            @Param("tenantId") Long tenantId,
            @Param("fechaDesde") Instant fechaDesde,
            @Param("fechaHasta") Instant fechaHasta,
            @Param("categoriaGastoId") Long categoriaGastoId,
            @Param("centroCostoId") Long centroCostoId,
            @Param("esProrrateable") Boolean esProrrateable,
            Pageable pageable
    );

    @Query("""
            SELECT g FROM GastoOperativo g
            WHERE g.tenantId = :tenantId
              AND g.esProrrateable = true
              AND (:fechaDesde IS NULL OR g.fechaRegistro >= :fechaDesde)
              AND (:fechaHasta IS NULL OR g.fechaRegistro <= :fechaHasta)
            """)
    List<GastoOperativo> findProrrateableGastos(
            @Param("tenantId") Long tenantId,
            @Param("fechaDesde") Instant fechaDesde,
            @Param("fechaHasta") Instant fechaHasta
    );
}
