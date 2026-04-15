package com.migestion.finance.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionContabilidadCostosRepository extends JpaRepository<ConfiguracionContabilidadCostos, Long> {

    Optional<ConfiguracionContabilidadCostos> findByTenantId(Long tenantId);
}
