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
@Table(name = "gasto_operativo")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GastoOperativo extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "empleado_id")
    private Long empleadoId;

    @Column(name = "centro_costo_id")
    private Long centroCostoId;

    @Column(name = "categoria_gasto_id")
    private Long categoriaGastoId;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal monto = BigDecimal.ZERO;

    @Column(name = "periodicidad", nullable = false, length = 50)
    @Builder.Default
    private String periodicidad = "unico";

    @Column(name = "fecha_registro", nullable = false)
    private Instant fechaRegistro;

    @Column(name = "es_recurrente", nullable = false)
    @Builder.Default
    private boolean esRecurrente = false;

    @Column(name = "es_directo", nullable = false)
    @Builder.Default
    private boolean esDirecto = true;

    @Column(name = "es_prorrateable", nullable = false)
    @Builder.Default
    private boolean esProrrateable = false;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
}
