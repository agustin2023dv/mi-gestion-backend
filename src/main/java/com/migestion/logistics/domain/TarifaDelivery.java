package com.migestion.logistics.domain;

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
@Table(name = "tarifa_delivery")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TarifaDelivery extends BaseEntity {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "tipo_calculo", nullable = false, length = 50)
    @Builder.Default
    private String tipoCalculo = "distancia";

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precioBase = BigDecimal.ZERO;

    @Column(name = "precio_por_km", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal precioPorKm = BigDecimal.ZERO;

    @Column(name = "distancia_minima_km", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal distanciaMinimaKm = BigDecimal.ZERO;

    @Column(name = "distancia_maxima_km", precision = 5, scale = 2)
    private BigDecimal distanciaMaximaKm;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}