package com.migestion.finance.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CriterioProrrateoRepository extends JpaRepository<CriterioProrrateo, Long> {

    Optional<CriterioProrrateo> findByIdAndTenantId(Long id, Long tenantId);

    List<CriterioProrrateo> findAllByTenantId(Long tenantId);
}
