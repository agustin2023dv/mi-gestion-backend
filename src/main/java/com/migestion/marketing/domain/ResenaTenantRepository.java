package com.migestion.marketing.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaTenantRepository extends JpaRepository<ResenaTenant, Long> {

    Optional<ResenaTenant> findByIdAndTenantId(Long id, Long tenantId);

    Page<ResenaTenant> findByTenantIdAndIsPublishedTrue(Long tenantId, Pageable pageable);

    Page<ResenaTenant> findByTenantIdAndRequiereModeracionTrue(Long tenantId, Pageable pageable);

    Optional<ResenaTenant> findByClienteIdAndTenantIdAndPedidoId(Long clienteId, Long tenantId, Long pedidoId);
}
