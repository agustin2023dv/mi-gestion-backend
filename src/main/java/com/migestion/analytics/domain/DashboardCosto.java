package com.migestion.analytics.domain;

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
@Table(name = "dashboardcosto")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DashboardCosto extends BaseEntity {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "cogs_acumulado", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal cogsAcumulado = BigDecimal.ZERO;

    @Column(name = "cogs_ingredientes", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal cogsIngredientes = BigDecimal.ZERO;

    @Column(name = "cogs_empleados", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal cogsEmpleados = BigDecimal.ZERO;

    @Column(name = "cogs_cif_asignado", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal cogsCifAsignado = BigDecimal.ZERO;

    @Column(name = "margen_bruto", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal margenBruto = BigDecimal.ZERO;

    @Column(name = "bep_unidades", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal bepUnidades = BigDecimal.ZERO;

    @Column(name = "bep_monto", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal bepMonto = BigDecimal.ZERO;

    @Column(name = "costos_empleados_periodo", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal costosEmpleadosPeriodo = BigDecimal.ZERO;

    @Column(name = "costos_ingredientes_periodo", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal costosIngredientesPeriodo = BigDecimal.ZERO;

    @Column(name = "gastos_fijos_totales", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal gastosFijosTotales = BigDecimal.ZERO;

    @Column(name = "fecha_calculo", nullable = false)
    private Instant fechaCalculo;
}