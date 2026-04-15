package com.migestion.finance.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CentroCostoRepository extends JpaRepository<CentroCosto, Long> {

    Optional<CentroCosto> findByIdAndTenantId(Long id, Long tenantId);

    List<CentroCosto> findAllByTenantId(Long tenantId);
}
