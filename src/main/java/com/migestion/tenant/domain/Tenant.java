package com.migestion.tenant.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "tenant",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_tenant_tenant_id", columnNames = {"tenant_id"}),
            @UniqueConstraint(name = "uq_tenant_slug", columnNames = {"slug"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Tenant extends BaseEntity {

    @Column(name = "tenant_id", nullable = false, unique = true, length = 100)
    private String tenantIdentifier;

    @Column(name = "nombre_negocio", nullable = false, length = 150)
    private String nombreNegocio;

    @Column(name = "slug", nullable = false, unique = true, length = 150)
    private String slug;

    /** FK to PlanSuscripcion (platform context — stored as id, not entity reference). */
    @Column(name = "plan_suscripcion_id")
    private Long planSuscripcionId;

    /** FK to UsuarioTenant — deferred per DDL to avoid circularity. */
    @Column(name = "propietario_id")
    private Long propietarioId;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "color_primario", length = 20)
    private String colorPrimario;

    @Column(name = "color_secundario", length = 20)
    private String colorSecundario;

    @Column(name = "visibilidad_publica", nullable = false)
    @Builder.Default
    private boolean visibilidadPublica = false;

    @Column(name = "acepta_reservas_servicios", nullable = false)
    @Builder.Default
    private boolean aceptaReservasServicios = false;

    @Column(name = "permite_pedidos_programados", nullable = false)
    @Builder.Default
    private boolean permitePedidosProgramados = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "is_suspended", nullable = false)
    @Builder.Default
    private boolean isSuspended = false;
}
