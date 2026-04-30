package com.migestion.scheduling.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcepcionHorarioRepository extends JpaRepository<ExcepcionHorario, Long> {

    List<ExcepcionHorario> findAllByTenantId(Long tenantId);

    Optional<ExcepcionHorario> findByIdAndTenantId(Long id, Long tenantId);

    List<ExcepcionHorario> findAllByTenantIdAndFechaBetween(Long tenantId, LocalDate from, LocalDate to);

    Optional<ExcepcionHorario> findByTenantIdAndFecha(Long tenantId, LocalDate fecha);
}
