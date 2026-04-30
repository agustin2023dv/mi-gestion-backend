package com.migestion.hr.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoDisponibilidadRepository extends JpaRepository<EmpleadoDisponibilidad, Long> {

    List<EmpleadoDisponibilidad> findAllByEmpleadoId(Long empleadoId);

    List<EmpleadoDisponibilidad> findAllByEmpleadoIdAndIsActiveTrue(Long empleadoId);
}
