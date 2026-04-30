package com.migestion.hr.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "empleado", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "email"}))
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Empleado extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "rol", nullable = false, length = 50)
    @Builder.Default
    private String rol = "staff";

    @Column(name = "tipo_remuneracion", nullable = false, length = 50)
    @Builder.Default
    private String tipoRemuneracion = "fijo";

    @Column(name = "monto_sueldo_fijo", precision = 12, scale = 2)
    private BigDecimal montoSueldoFijo;

    @Column(name = "frecuencia_pago", length = 20)
    @Builder.Default
    private String frecuenciaPago = "mensual";

    @Column(name = "costo_hora_base", precision = 10, scale = 2)
    private BigDecimal costoHoraBase;

    @Column(name = "porcentaje_comision", precision = 5, scale = 2)
    private BigDecimal porcentajeComision;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_egreso")
    private LocalDate fechaEgreso;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
