package com.migestion.orders.domain;

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

@Entity
@Table(name = "pedidoitem")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PedidoItem extends BaseEntity {

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "cantidad", nullable = false)
    @Builder.Default
    private Integer cantidad = 1;

    @Column(name = "precio_base_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBaseSnapshot;

    @Column(name = "precio_extras_snapshot", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precioExtrasSnapshot = BigDecimal.ZERO;

    @Column(name = "costo_unitario_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitarioSnapshot;

    /**
     * Computed by DB as: cantidad * (precio_base_snapshot + precio_extras_snapshot).
     * Mapped read-only; computed in code via {@link #calcularSubtotal()}.
     */
    @Column(name = "subtotal", precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal subtotal;

    @Column(name = "nombre_producto_snapshot", nullable = false, length = 150)
    private String nombreProductoSnapshot;

    @Column(name = "sku_snapshot", length = 100)
    private String skuSnapshot;

    /**
     * Computes the subtotal in application code, mirroring the DB generated column formula.
     */
    public BigDecimal calcularSubtotal() {
        if (precioBaseSnapshot == null) return BigDecimal.ZERO;
        BigDecimal extras = precioExtrasSnapshot != null ? precioExtrasSnapshot : BigDecimal.ZERO;
        return new BigDecimal(cantidad).multiply(precioBaseSnapshot.add(extras));
    }
}
