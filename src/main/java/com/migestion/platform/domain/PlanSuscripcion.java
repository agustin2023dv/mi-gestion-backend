package com.migestion.platform.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "plan_suscripcion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlanSuscripcion extends BaseEntity {

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "max_productos", nullable = false)
    @Builder.Default
    private int maxProductos = 0;

    @Column(name = "max_pedidos_mensuales", nullable = false)
    @Builder.Default
    private int maxPedidosMensuales = 0;

    @Column(name = "max_almacenamiento_mb", nullable = false)
    @Builder.Default
    private int maxAlmacenamientoMb = 0;

    @Column(name = "precio_mensual", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precioMensual = BigDecimal.ZERO;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private List<String> features = List.of();

    @Column(name = "order_level", nullable = false)
    @Builder.Default
    private int orderLevel = 0;
}
