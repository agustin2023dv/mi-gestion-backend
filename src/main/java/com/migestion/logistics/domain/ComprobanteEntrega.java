package com.migestion.logistics.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "comprobante_entrega")
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
public class ComprobanteEntrega extends BaseEntity {

    @Column(name = "entrega_id")
    private Long entregaId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "hash_criptografico", length = 128)
    private String hashCriptografico;

    @Column(name = "firma_cliente", columnDefinition = "TEXT")
    private String firmaCliente;

    @Column(name = "firma_repartidor", columnDefinition = "TEXT")
    private String firmaRepartidor;

    @Column(name = "latitud_entrega", precision = 10, scale = 8)
    private BigDecimal latitudEntrega;

    @Column(name = "longitud_entrega", precision = 11, scale = 8)
    private BigDecimal longitudEntrega;

    @Column(name = "fecha_entrega", nullable = false)
    @Builder.Default
    private Instant fechaEntrega = Instant.now();

    @Column(name = "sello_temporal", length = 50)
    private String selloTemporal;

    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean isVerified = false;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;
}
