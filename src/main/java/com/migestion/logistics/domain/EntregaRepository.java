package com.migestion.logistics.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    Optional<Entrega> findByIdAndTenantId(Long id, Long tenantId);

    List<Entrega> findAllByTenantId(Long tenantId);
}
