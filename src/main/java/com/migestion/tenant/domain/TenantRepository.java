package com.migestion.tenant.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByTenantIdentifier(String tenantIdentifier);

    Optional<Tenant> findBySlug(String slug);
}
