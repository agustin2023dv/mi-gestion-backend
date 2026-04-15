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
@Table(name = "entrega")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Entrega extends BaseEntity {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "repartidor_id")
    private Long repartidorId;

    @Column(name = "estado", nullable = false, length = 50)
    @Builder.Default
    private String estado = "pendiente";

    @Column(name = "latitud_actual", precision = 10, scale = 8)
    private BigDecimal latitudActual;

    @Column(name = "longitud_actual", precision = 11, scale = 8)
    private BigDecimal longitudActual;

    @Column(name = "distancia_verificada", precision = 8, scale = 2)
    private BigDecimal distanciaVerificada;

    @Column(name = "geolocalizacion_validada", nullable = false)
    @Builder.Default
    private boolean geolocalizacionValidada = false;

    @Column(name = "verificacion_edad_hecha", nullable = false)
    @Builder.Default
    private boolean verificacionEdadHecha = false;

    @Column(name = "tipo_documento_entregante", length = 50)
    private String tipoDocumentoEntregante;

    @Column(name = "hash_documento", length = 255)
    private String hashDocumento;

    @Column(name = "fecha_verificacion_edad")
    private Instant fechaVerificacionEdad;

    @Column(name = "asignado_en")
    private Instant asignadoEn;

    @Column(name = "inicio_entrega")
    private Instant inicioEntrega;

    @Column(name = "entrega_confirmada")
    private Instant entregaConfirmada;
}
