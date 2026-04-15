package com.migestion.notifications.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    Optional<Notificacion> findByIdAndTenantId(Long id, Long tenantId);

    List<Notificacion> findByTenantIdAndUsuarioIdAndUsuarioTipoOrderByCreatedAtDesc(
            Long tenantId,
            Long usuarioId,
            String usuarioTipo
    );
}
