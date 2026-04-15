package com.migestion.finance.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaGastoRepository extends JpaRepository<CategoriaGasto, Long> {

    Optional<CategoriaGasto> findByIdAndTenantId(Long id, Long tenantId);

    List<CategoriaGasto> findAllByTenantId(Long tenantId);
}
