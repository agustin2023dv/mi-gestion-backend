package com.migestion.analytics.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardVentaRepository extends JpaRepository<DashboardVenta, Long> {

    Optional<DashboardVenta> findByIdAndTenantId(Long id, Long tenantId);

    List<DashboardVenta> findAllByTenantId(Long tenantId);
}