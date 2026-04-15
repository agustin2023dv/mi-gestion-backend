package com.migestion.orders.infrastructure.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.migestion.notifications.application.SendNotificationUseCase;
import com.migestion.orders.application.DeliveryCreationPort;
import com.migestion.orders.domain.EstadoPedido;
import com.migestion.orders.domain.EstadoPedidoRepository;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoEstadoHistorial;
import com.migestion.orders.domain.PedidoEstadoHistorialRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.domain.event.OrderPaidEvent;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class OrderPaidListenerTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private EstadoPedidoRepository estadoPedidoRepository;

    @Mock
    private PedidoEstadoHistorialRepository pedidoEstadoHistorialRepository;

    @Mock
    private DeliveryCreationPort deliveryCreationPort;

    @Mock
    private SendNotificationUseCase sendNotificationUseCase;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UsuarioTenantRepository usuarioTenantRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private OrderPaidListener orderPaidListener;

    @Test
    void shouldMarkOrderAsPaidAndConfirmPendingOrder() {
        EstadoPedido pendingState = EstadoPedido.builder()
                .id(1L)
                .codigo("PENDING")
                .nombre("Pending")
                .build();
        EstadoPedido confirmedState = EstadoPedido.builder()
                .id(2L)
                .codigo("CONFIRMED")
                .nombre("Confirmed")
                .build();
        Pedido pedido = Pedido.builder()
                .tenantId(20L)
                .estado(pendingState)
                .estadoPago("pendiente")
                .build();
        OrderPaidEvent event = OrderPaidEvent.builder()
                .pedidoId(10L)
                .tenantId(20L)
                .monto(new BigDecimal("150.00"))
                .transactionId("pi_123")
                .build();

        when(pedidoRepository.findByIdAndTenantId(10L, 20L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findByCodigo("CONFIRMED")).thenReturn(Optional.of(confirmedState));

        orderPaidListener.handle(event);

        assertEquals("PAID", pedido.getEstadoPago());
        assertSame(confirmedState, pedido.getEstado());

        ArgumentCaptor<PedidoEstadoHistorial> historialCaptor = ArgumentCaptor.forClass(PedidoEstadoHistorial.class);
        verify(pedidoEstadoHistorialRepository).save(historialCaptor.capture());
        assertSame(confirmedState, historialCaptor.getValue().getEstado());
        assertEquals("Pedido confirmado tras la acreditacion del pago pi_123", historialCaptor.getValue().getNotas());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void shouldOnlyMarkOrderAsPaidWhenOrderIsNotPending() {
        EstadoPedido preparingState = EstadoPedido.builder()
                .id(3L)
                .codigo("PREPARING")
                .nombre("Preparing")
                .build();
        Pedido pedido = Pedido.builder()
                .tenantId(21L)
                .estado(preparingState)
                .estadoPago("pendiente")
                .build();
        OrderPaidEvent event = OrderPaidEvent.builder()
                .pedidoId(11L)
                .tenantId(21L)
                .monto(new BigDecimal("90.00"))
                .transactionId("pi_456")
                .build();

        when(pedidoRepository.findByIdAndTenantId(11L, 21L)).thenReturn(Optional.of(pedido));

        orderPaidListener.handle(event);

        assertEquals("PAID", pedido.getEstadoPago());
        assertSame(preparingState, pedido.getEstado());
        verify(estadoPedidoRepository, never()).findByCodigo(any());
        verify(pedidoEstadoHistorialRepository, never()).save(any());
        verify(pedidoRepository).save(pedido);
    }
}
