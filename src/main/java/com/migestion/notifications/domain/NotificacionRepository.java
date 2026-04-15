package com.migestion.notifications.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Notificacion n
               set n.leido = true
             where n.tenantId = :tenantId
               and n.usuarioId = :usuarioId
               and n.usuarioTipo = :usuarioTipo
               and n.leido = false
            """)
    int markAllAsRead(
            @Param("tenantId") Long tenantId,
            @Param("usuarioId") Long usuarioId,
            @Param("usuarioTipo") String usuarioTipo
    );
}
