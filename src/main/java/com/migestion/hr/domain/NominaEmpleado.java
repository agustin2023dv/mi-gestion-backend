package com.migestion.hr.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "nomina_empleado")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NominaEmpleado extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Column(name = "dias_trabajados")
    private Integer diasTrabajados;

    @Column(name = "horas_trabajadas", precision = 8, scale = 2)
    private BigDecimal horasTrabajadas;

    @Column(name = "sueldo_base_calculado", precision = 12, scale = 2)
    private BigDecimal sueldoBaseCalculado;

    @Column(name = "comisiones_generadas", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal comisionesGeneradas = BigDecimal.ZERO;

    @Column(name = "bonificaciones", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal bonificaciones = BigDecimal.ZERO;

    @Column(name = "descuentos", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal descuentos = BigDecimal.ZERO;

    @Column(name = "total_a_pagar", precision = 12, scale = 2)
    private BigDecimal totalAPagar;

    @Column(name = "is_pagado", nullable = false)
    @Builder.Default
    private boolean isPagado = false;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "estado", nullable = false, length = 30)
    @Builder.Default
    private String estado = "borrador";
}
