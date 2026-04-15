package com.migestion.notifications.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "notificacion")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notificacion extends BaseEntity {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_tipo", length = 50)
    private String usuarioTipo;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "canal", nullable = false, length = 50)
    private String canal;

    @Column(name = "titulo", length = 200)
    private String titulo;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "data_json", columnDefinition = "jsonb")
    private String dataJson;

    @Column(name = "leido", nullable = false)
    @Builder.Default
    private boolean leido = false;

    @Column(name = "enviado_en")
    private Instant enviadoEn;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;
}
