package com.migestion.tenant.domain;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioTenantRepository extends JpaRepository<UsuarioTenant, Long> {

    boolean existsByEmailIgnoreCase(String email);

    List<UsuarioTenant> findAllByIdIn(Collection<Long> ids);
}
