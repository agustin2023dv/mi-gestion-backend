package com.migestion.payments.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.ParamDef;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pago")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pago extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "turno_id")
    private Long turnoId;

    @Column(name = "empleado_id")
    private Long empleadoId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "tipo_transaccion", nullable = false, length = 50)
    @Builder.Default
    private String tipoTransaccion = "pago";

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "moneda", nullable = false, length = 10)
    @Builder.Default
    private String moneda = "USD";

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalle_pago", columnDefinition = "jsonb")
    private Map<String, Object> detallePago;

    @Column(name = "referencia_reembolso", length = 255)
    private String referenciaReembolso;

    @Column(name = "fecha_procesamiento")
    private Instant fechaProcesamiento;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "respuesta_provider", columnDefinition = "jsonb")
    private Map<String, Object> respuestaProvider;
}
