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
        name = "UsuarioTenant",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_usuariotenant_tenant_email", columnNames = {"tenant_id", "email"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UsuarioTenant extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "segundo_nombre", length = 100)
    private String segundoNombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "caracteristica_telefono_zona", length = 10)
    private String caracteristicaTelefonoZona;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "sexo", length = 20)
    private String sexo;

    @Column(name = "direccion_id")
    private Long direccionId;

    @Column(name = "rol", nullable = false, length = 50)
    @Builder.Default
    private String rol = "admin";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
