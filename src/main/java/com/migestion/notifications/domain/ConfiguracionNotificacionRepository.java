package com.migestion.notifications.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionNotificacionRepository extends JpaRepository<ConfiguracionNotificacion, Long> {

    Optional<ConfiguracionNotificacion> findByTenantId(Long tenantId);
}
