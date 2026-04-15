package com.migestion.analytics.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimuladorEscenarioRepository extends JpaRepository<SimuladorEscenario, Long> {

    Optional<SimuladorEscenario> findByIdAndTenantId(Long id, Long tenantId);

    List<SimuladorEscenario> findAllByTenantId(Long tenantId);
}