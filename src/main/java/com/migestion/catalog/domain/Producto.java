package com.migestion.catalog.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(
        name = "producto",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_producto_tenant_sku", columnNames = {"tenant_id", "sku"})
        }
)
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Producto extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "subcategoria_id")
    private Long subcategoriaId;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precio = BigDecimal.ZERO;

    @Column(name = "costo_unitario_calculado", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoUnitarioCalculado = BigDecimal.ZERO;

    @Column(name = "costo_unitario_manual_override", precision = 10, scale = 2)
    private BigDecimal costoUnitarioManualOverride;

    @Column(name = "usa_costo_calculado", nullable = false)
    @Builder.Default
    private boolean usaCostoCalculado = true;

    @Column(name = "stock", nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "stock_reservado", nullable = false)
    @Builder.Default
    private Integer stockReservado = 0;

    @Column(name = "stock_minimo", nullable = false)
    @Builder.Default
    private Integer stockMinimo = 0;

    @Column(name = "imagen_url", columnDefinition = "TEXT")
    private String imagenUrl;

    @Column(name = "sku", length = 100)
    private String sku;

    @Column(name = "es_personalizable", nullable = false)
    @Builder.Default
    private boolean esPersonalizable = false;

    @Column(name = "es_servicio", nullable = false)
    @Builder.Default
    private boolean esServicio = false;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "permite_booking", nullable = false)
    @Builder.Default
    private boolean permiteBooking = false;

    @Column(name = "buffer_entre_turnos_min")
    @Builder.Default
    private Integer bufferEntreTurnosMin = 0;

    @Column(name = "requiere_verificacion_edad", nullable = false)
    @Builder.Default
    private boolean requiereVerificacionEdad = false;

    @Column(name = "edad_minima")
    private Integer edadMinima;

    @Column(name = "requiere_empleado_especifico", nullable = false)
    @Builder.Default
    private boolean requiereEmpleadoEspecifico = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}