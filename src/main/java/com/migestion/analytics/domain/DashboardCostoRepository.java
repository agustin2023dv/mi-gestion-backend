package com.migestion.analytics.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardCostoRepository extends JpaRepository<DashboardCosto, Long> {

    Optional<DashboardCosto> findByIdAndTenantId(Long id, Long tenantId);

    List<DashboardCosto> findAllByTenantId(Long tenantId);
}