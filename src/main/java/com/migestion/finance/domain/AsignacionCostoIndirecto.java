package com.migestion.finance.domain;

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
@Table(name = "asignacion_costo_indirecto")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AsignacionCostoIndirecto extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "gasto_operativo_id", nullable = false)
    private Long gastoOperativoId;

    @Column(name = "criterio_prorrateo_id", nullable = false)
    private Long criterioProrrateoId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "centro_costo_id", nullable = false)
    private Long centroCostoId;

    @Column(name = "monto_asignado", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal montoAsignado = BigDecimal.ZERO;

    @Column(name = "periodo_inicio", nullable = false)
    private Instant periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private Instant periodoFin;

    @Column(name = "fecha_calculo", nullable = false)
    private Instant fechaCalculo;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;
}
