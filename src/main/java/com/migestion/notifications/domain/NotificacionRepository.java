package com.migestion.notifications.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    Optional<Notificacion> findByIdAndTenantId(Long id, Long tenantId);

    Optional<Notificacion> findByIdAndTenantIdAndUsuarioId(Long id, Long tenantId, Long usuarioId);

    Page<Notificacion> findByTenantIdAndUsuarioIdAndUsuarioTipoOrderByCreatedAtDesc(
            Long tenantId,
            Long usuarioId,
            String usuarioTipo,
            Pageable pageable
    );

    List<Notificacion> findByTenantIdAndUsuarioIdAndUsuarioTipoOrderByCreatedAtDesc(
            Long tenantId,
            Long usuarioId,
            String usuarioTipo
    );
}
