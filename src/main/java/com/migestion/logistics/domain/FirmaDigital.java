package com.migestion.logistics.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.ParamDef;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "firma_digital")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(
        name = "tenantFilter",
        condition = "exists (select 1 from entrega e where e.id = entrega_id and e.tenant_id = :tenantId)"
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FirmaDigital extends BaseEntity {

    @Column(name = "entrega_id")
    private Long entregaId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "token_unico", nullable = false)
    @Builder.Default
    private UUID tokenUnico = UUID.randomUUID();

    @Column(name = "qr_code_data", columnDefinition = "TEXT")
    private String qrCodeData;

    @Column(name = "firma_datos", columnDefinition = "TEXT")
    private String firmaDatos;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dispositivo_info", columnDefinition = "jsonb")
    private Map<String, Object> dispositivoInfo;

    @Column(name = "generado_en", nullable = false)
    @Builder.Default
    private Instant generadoEn = Instant.now();

    @Column(name = "firmado_en")
    private Instant firmadoEn;

    @Column(name = "expirado", nullable = false)
    @Builder.Default
    private boolean expirado = false;
}
