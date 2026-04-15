package com.migestion.platform.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanSuscripcionRepository extends JpaRepository<PlanSuscripcion, Long> {

    Optional<PlanSuscripcion> findByNombreIgnoreCase(String nombre);
}
