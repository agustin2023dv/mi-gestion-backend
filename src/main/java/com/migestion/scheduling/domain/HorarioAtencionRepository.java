package com.migestion.scheduling.domain;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, Long> {

    List<HorarioAtencion> findAllByTenantId(Long tenantId);

    Optional<HorarioAtencion> findByIdAndTenantId(Long id, Long tenantId);

    Optional<HorarioAtencion> findByTenantIdAndDiaSemana(Long tenantId, DayOfWeek diaSemana);

    List<HorarioAtencion> findAllByTenantIdAndIsActivoTrue(Long tenantId);
}
