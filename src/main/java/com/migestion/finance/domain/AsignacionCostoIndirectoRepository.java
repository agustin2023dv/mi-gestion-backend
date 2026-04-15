package com.migestion.finance.domain;

import com.migestion.finance.dto.HistorialProrrateoResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AsignacionCostoIndirectoRepository extends JpaRepository<AsignacionCostoIndirecto, Long> {

    Optional<AsignacionCostoIndirecto> findByIdAndTenantId(Long id, Long tenantId);

    List<AsignacionCostoIndirecto> findAllByTenantId(Long tenantId);

    void deleteByTenantIdAndPeriodoInicioAndPeriodoFin(Long tenantId, Instant periodoInicio, Instant periodoFin);

    @Query("""
            SELECT new com.migestion.finance.dto.HistorialProrrateoResponse(
                a.fechaCalculo,
                a.periodoInicio,
                a.periodoFin,
                a.criterioProrrateoId,
                COUNT(a),
                COALESCE(SUM(a.montoAsignado), 0)
            )
            FROM AsignacionCostoIndirecto a
            WHERE a.tenantId = :tenantId
            GROUP BY a.fechaCalculo, a.periodoInicio, a.periodoFin, a.criterioProrrateoId
            ORDER BY a.fechaCalculo DESC
            """)
    Page<HistorialProrrateoResponse> findHistorialByTenantId(
            @Param("tenantId") Long tenantId,
            Pageable pageable
    );
}
