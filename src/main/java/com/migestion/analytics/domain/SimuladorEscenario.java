package com.migestion.analytics.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "simuladorescenario")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimuladorEscenario extends BaseEntity {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "nombre_escenario", nullable = false, length = 100)
    private String nombreEscenario;

    @Column(name = "precio_promedio_simulado", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precioPromedioSimulado = BigDecimal.ZERO;

    @Column(name = "costo_variable_simulado", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoVariableSimulado = BigDecimal.ZERO;

    @Column(name = "costos_empleados_simulado", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal costosEmpleadosSimulado = BigDecimal.ZERO;

    @Column(name = "costos_ingredientes_simulado", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal costosIngredientesSimulado = BigDecimal.ZERO;

    @Column(name = "gastos_fijos_simulado", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal gastosFijosSimulado = BigDecimal.ZERO;

    @Column(name = "margen_simulado", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal margenSimulado = BigDecimal.ZERO;

    @Column(name = "bep_simulado", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal bepSimulado = BigDecimal.ZERO;

    @Column(name = "is_saved", nullable = false)
    @Builder.Default
    private boolean isSaved = false;
}