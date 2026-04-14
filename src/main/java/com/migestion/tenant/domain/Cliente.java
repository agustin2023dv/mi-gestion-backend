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
import org.hibernate.annotations.Filter;

@Entity
@Table(
        name = "cliente",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_cliente_tenant_email", columnNames = {"tenant_id", "email"})
        }
)
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Cliente extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "email_verificado", nullable = false)
    @Builder.Default
    private boolean emailVerificado = false;
}