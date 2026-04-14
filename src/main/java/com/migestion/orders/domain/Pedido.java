package com.migestion.orders.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(
        name = "pedido",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_pedido_idempotency_key", columnNames = {"idempotency_key"})
        }
)
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pedido extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "repartidor_id")
    private Long repartidorId;

    @Column(name = "direccion_entrega_id")
    private Long direccionEntregaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoPedido estado;

    @Column(name = "tarifa_delivery_id")
    private Long tarifaDeliveryId;

    @Column(name = "config_pedidos_programados_id")
    private Long configPedidosProgramadosId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Column(name = "numero_pedido", nullable = false, length = 50)
    private String numeroPedido;

    @Column(name = "tipo_entrega", nullable = false, length = 50)
    @Builder.Default
    private String tipoEntrega = "domicilio";

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "impuestos", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Column(name = "costo_envio", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(name = "descuento_inicial", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal descuentoInicial = BigDecimal.ZERO;

    @Column(name = "descuento_adicional", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal descuentoAdicional = BigDecimal.ZERO;

    @Column(name = "motivo_descuento_adicional", columnDefinition = "TEXT")
    private String motivoDescuentoAdicional;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "estado_pago", nullable = false, length = 50)
    @Builder.Default
    private String estadoPago = "pendiente";

    @Column(name = "origen_pedido", length = 50)
    private String origenPedido;

    @Column(name = "tracking_token", length = 100)
    private String trackingToken;

    @Column(name = "codigo_entrega", length = 100)
    private String codigoEntrega;

    @Column(name = "tipo_codigo_entrega", length = 50)
    private String tipoCodigoEntrega;

    @Column(name = "requiere_verificacion_edad", nullable = false)
    @Builder.Default
    private boolean requiereVerificacionEdad = false;

    @Column(name = "es_programado", nullable = false)
    @Builder.Default
    private boolean esProgramado = false;

    @Column(name = "fecha_hora_programada")
    private Instant fechaHoraProgramada;

    @Column(name = "estado_programacion", length = 50)
    private String estadoProgramacion;

    @Column(name = "fecha_pedido", nullable = false)
    @Builder.Default
    private Instant fechaPedido = Instant.now();

    @Column(name = "fecha_entrega_solicitada")
    private Instant fechaEntregaSolicitada;

    @Column(name = "notas_cliente", columnDefinition = "TEXT")
    private String notasCliente;

    @Column(name = "notas_administrativas", columnDefinition = "TEXT")
    private String notasAdministrativas;

    @Column(name = "motivo_cancelacion", columnDefinition = "TEXT")
    private String motivoCancelacion;

    @Column(name = "usuario_cancelacion_id")
    private Long usuarioCancelacionId;

    @Column(name = "usuario_cancelacion_tipo", length = 50)
    private String usuarioCancelacionTipo;

    @Column(name = "fecha_cancelacion")
    private Instant fechaCancelacion;

    @Column(name = "es_modificado", nullable = false)
    @Builder.Default
    private boolean esModificado = false;

    @Column(name = "cantidad_modificaciones", nullable = false)
    @Builder.Default
    private Integer cantidadModificaciones = 0;

    @Column(name = "fecha_ultima_modificacion")
    private Instant fechaUltimaModificacion;

    @Column(name = "pickup_codigo_retiro", length = 100)
    private String pickupCodigoRetiro;

    @Column(name = "pickup_listo_en")
    private Instant pickupListoEn;
}
