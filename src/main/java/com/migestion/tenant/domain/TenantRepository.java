package com.migestion.tenant.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByTenantIdentifier(String tenantIdentifier);

    Optional<Tenant> findBySlug(String slug);

    @Query("""
            SELECT t FROM Tenant t
            WHERE (:isActive IS NULL OR t.isActive = :isActive)
              AND (:isSuspended IS NULL OR t.isSuspended = :isSuspended)
              AND (:planSuscripcionId IS NULL OR t.planSuscripcionId = :planSuscripcionId)
              AND (:search IS NULL
                   OR LOWER(t.nombreNegocio) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(t.slug) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(t.tenantIdentifier) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Tenant> findAllWithFilters(
            @Param("isActive") Boolean isActive,
            @Param("isSuspended") Boolean isSuspended,
            @Param("planSuscripcionId") Long planSuscripcionId,
            @Param("search") String search,
            Pageable pageable);
}
