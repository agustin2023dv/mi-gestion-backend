package com.migestion.notifications.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "configuracionnotificacion")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfiguracionNotificacion extends BaseEntity {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private boolean emailEnabled = true;

    @Column(name = "whatsapp_enabled", nullable = false)
    @Builder.Default
    private boolean whatsappEnabled = false;

    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private boolean pushEnabled = false;

    @Column(name = "whatsapp_numero", length = 50)
    private String whatsappNumero;

    @Column(name = "email_destino", length = 255)
    private String emailDestino;
}