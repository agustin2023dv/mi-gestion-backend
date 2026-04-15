package com.migestion.finance.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "configuracion_contabilidad_costos")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfiguracionContabilidadCostos extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "incluye_mano_obra_directa_en_costo", nullable = false)
    @Builder.Default
    private boolean incluyeManoObraDirectaEnCosto = true;

    @Column(name = "incluye_cif_en_costo", nullable = false)
    @Builder.Default
    private boolean incluyeCifEnCosto = false;

    @Column(name = "incluye_impuestos_en_costo", nullable = false)
    @Builder.Default
    private boolean incluyeImpuestosEnCosto = true;

    @Column(name = "metodo_valoracion_inventario", nullable = false, length = 50)
    @Builder.Default
    private String metodoValoracionInventario = "promedio_ponderado";

    @Column(name = "moneda_funcional", nullable = false, length = 10)
    @Builder.Default
    private String monedaFuncional = "USD";
}
