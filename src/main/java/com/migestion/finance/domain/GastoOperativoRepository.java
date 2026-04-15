package com.migestion.finance.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Long> {

    Optional<GastoOperativo> findByIdAndTenantId(Long id, Long tenantId);

    List<GastoOperativo> findAllByTenantId(Long tenantId);
}
