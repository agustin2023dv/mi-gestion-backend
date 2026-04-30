package com.migestion.booking.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findAllByTenantId(Long tenantId);

    Optional<Turno> findByIdAndTenantId(Long id, Long tenantId);

    List<Turno> findAllByTenantIdAndEstado(Long tenantId, String estado);

    List<Turno> findAllByClienteIdAndTenantId(Long clienteId, Long tenantId);

    @Query("SELECT t FROM Turno t WHERE t.tenantId = :tenantId "
         + "AND t.empleadoId = :empleadoId "
         + "AND t.esCancelado = false "
         + "AND t.fechaHoraInicio < :fin "
         + "AND t.fechaHoraFin > :inicio")
    List<Turno> findOverlapping(
            @Param("tenantId") Long tenantId,
            @Param("empleadoId") Long empleadoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
