package com.migestion.tenant.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioTenantRepository extends JpaRepository<UsuarioTenant, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<UsuarioTenant> findByEmailIgnoreCase(String email);

    List<UsuarioTenant> findAllByIdIn(Collection<Long> ids);

    Optional<UsuarioTenant> findByIdAndTenantId(Long id, Long tenantId);
}
