package com.migestion.marketing.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaTenantRepository extends JpaRepository<ResenaTenant, Long> {

    Optional<ResenaTenant> findByIdAndTenantId(Long id, Long tenantId);
}
