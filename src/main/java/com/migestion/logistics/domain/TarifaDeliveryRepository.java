package com.migestion.logistics.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaDeliveryRepository extends JpaRepository<TarifaDelivery, Long> {

    Optional<TarifaDelivery> findByIdAndTenantId(Long id, Long tenantId);

    List<TarifaDelivery> findAllByTenantId(Long tenantId);
}
