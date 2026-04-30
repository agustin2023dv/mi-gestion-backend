package com.migestion.scheduling.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueoHorarioRepository extends JpaRepository<BloqueoHorario, Long> {

    List<BloqueoHorario> findAllByTenantId(Long tenantId);

    Optional<BloqueoHorario> findByIdAndTenantId(Long id, Long tenantId);

    List<BloqueoHorario> findAllByTenantIdAndFecha(Long tenantId, LocalDate fecha);
}
