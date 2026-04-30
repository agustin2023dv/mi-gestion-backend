package com.migestion.hr.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    List<Empleado> findAllByTenantId(Long tenantId);

    Optional<Empleado> findByIdAndTenantId(Long id, Long tenantId);

    Optional<Empleado> findByTenantIdAndEmailIgnoreCase(Long tenantId, String email);

    List<Empleado> findAllByTenantIdAndIsActiveTrue(Long tenantId);
}
