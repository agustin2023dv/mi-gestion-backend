package com.migestion.booking.domain;

import com.migestion.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
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
@Table(name = "turno")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Turno extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "empleado_id")
    private Long empleadoId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(name = "estado", nullable = false, length = 30)
    @Builder.Default
    private String estado = "pendiente";

    @Column(name = "tipo_confirmacion", length = 30)
    @Builder.Default
    private String tipoConfirmacion = "automatica";

    @Column(name = "codigo_confirmacion", length = 20)
    private String codigoConfirmacion;

    @Column(name = "notas_cliente", length = 500)
    private String notasCliente;

    @Column(name = "notas_tenant", length = 500)
    private String notasTenant;

    @Column(name = "requiere_recordatorio", nullable = false)
    @Builder.Default
    private boolean requiereRecordatorio = true;

    @Column(name = "recordatorio_enviado", nullable = false)
    @Builder.Default
    private boolean recordatorioEnviado = false;

    @Column(name = "no_show", nullable = false)
    @Builder.Default
    private boolean noShow = false;

    @Column(name = "precio_aplicado", precision = 12, scale = 2)
    private BigDecimal precioAplicado;

    @Column(name = "politica_pago", length = 30)
    @Builder.Default
    private String politicaPago = "completo";

    @Column(name = "monto_sena_requerido", precision = 12, scale = 2)
    private BigDecimal montoSenaRequerido;

    @Column(name = "es_reembolsable", nullable = false)
    @Builder.Default
    private boolean esReembolsable = true;

    @Column(name = "fecha_limite_cancelacion_gratis")
    private LocalDateTime fechaLimiteCancelacionGratis;

    @Column(name = "fecha_limite_cancelacion_parcial")
    private LocalDateTime fechaLimiteCancelacionParcial;

    @Column(name = "porcentaje_penalizacion", precision = 5, scale = 2)
    private BigDecimal porcentajePenalizacion;

    @Column(name = "monto_abonado", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal montoAbonado = BigDecimal.ZERO;

    @Column(name = "monto_reembolsado", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal montoReembolsado = BigDecimal.ZERO;

    @Column(name = "estado_pago", length = 30)
    @Builder.Default
    private String estadoPago = "pendiente";

    @Column(name = "fecha_pago_confirmado")
    private Instant fechaPagoConfirmado;

    @Column(name = "es_cancelado", nullable = false)
    @Builder.Default
    private boolean esCancelado = false;

    @Column(name = "cancelado_por_usuario_id")
    private Long canceladoPorUsuarioId;

    @Column(name = "cancelado_por_tipo", length = 30)
    private String canceladoPorTipo;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @Column(name = "fecha_cancelacion")
    private Instant fechaCancelacion;

    @Column(name = "modo_asignacion", length = 30)
    @Builder.Default
    private String modoAsignacion = "automatico";

    @Column(name = "asignacion_manual_override", nullable = false)
    @Builder.Default
    private boolean asignacionManualOverride = false;

    @Column(name = "asignado_en")
    private Instant asignadoEn;

    @Column(name = "costo_empleado_snapshot", precision = 12, scale = 2)
    private BigDecimal costoEmpleadoSnapshot;

    @Column(name = "es_modificado", nullable = false)
    @Builder.Default
    private boolean esModificado = false;

    @Column(name = "cantidad_modificaciones", nullable = false)
    @Builder.Default
    private int cantidadModificaciones = 0;

    @Column(name = "fecha_ultima_modificacion")
    private Instant fechaUltimaModificacion;
}
