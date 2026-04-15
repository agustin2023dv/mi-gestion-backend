package com.migestion.finance.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignacionCostoIndirectoRepository extends JpaRepository<AsignacionCostoIndirecto, Long> {

    Optional<AsignacionCostoIndirecto> findByIdAndTenantId(Long id, Long tenantId);

    List<AsignacionCostoIndirecto> findAllByTenantId(Long tenantId);
}
