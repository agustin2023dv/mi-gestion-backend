package com.migestion.hr.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NominaEmpleadoRepository extends JpaRepository<NominaEmpleado, Long> {

    List<NominaEmpleado> findAllByTenantId(Long tenantId);

    Optional<NominaEmpleado> findByIdAndTenantId(Long id, Long tenantId);

    List<NominaEmpleado> findAllByEmpleadoId(Long empleadoId);
}
