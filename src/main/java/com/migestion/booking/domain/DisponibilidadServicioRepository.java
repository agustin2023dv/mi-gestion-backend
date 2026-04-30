package com.migestion.booking.domain;

import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisponibilidadServicioRepository extends JpaRepository<DisponibilidadServicio, Long> {

    List<DisponibilidadServicio> findAllByTenantIdAndProductoId(Long tenantId, Long productoId);

    List<DisponibilidadServicio> findAllByTenantIdAndProductoIdAndDiaSemana(
            Long tenantId, Long productoId, DayOfWeek diaSemana);

    List<DisponibilidadServicio> findAllByTenantIdAndProductoIdAndIsActiveTrue(
            Long tenantId, Long productoId);
}
