package com.migestion.orders.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoEstadoHistorial;
import com.migestion.orders.domain.PedidoEstadoHistorialRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.dto.PedidoTrackingResponse;
import com.migestion.orders.infrastructure.mapper.PedidoMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrackPedidoUseCase")
class TrackPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private PedidoEstadoHistorialRepository historialRepository;
    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private TrackPedidoUseCase trackPedidoUseCase;

    @Test
    @DisplayName("Should throw not found when tracking token does not match any pedido")
    void should_throw_not_found_when_tracking_token_does_not_match_any_pedido() {
        // Arrange
        when(pedidoRepository.findByTrackingToken("token-missing")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> trackPedidoUseCase.execute("token-missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pedido with id token-missing not found");

        verify(historialRepository, never()).findAllByPedidoIdOrderByFechaCambioAsc(1L);
        verify(pedidoMapper, never()).toTrackingResponse(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    @DisplayName("Should map tracking response using pedido and ordered historial")
    void should_map_tracking_response_using_pedido_and_ordered_historial() {
        // Arrange
        Pedido pedido = org.mockito.Mockito.mock(Pedido.class);
        List<PedidoEstadoHistorial> historial = List.of(
                PedidoEstadoHistorial.builder().pedidoId(25L).fechaCambio(Instant.parse("2026-04-01T10:15:30Z")).build(),
                PedidoEstadoHistorial.builder().pedidoId(25L).fechaCambio(Instant.parse("2026-04-01T10:20:30Z")).build()
        );
        PedidoTrackingResponse expectedResponse = PedidoTrackingResponse.builder()
                .numeroPedido("PED-25")
                .estadoActual("en_camino")
                .codigoEntrega("ABC123")
                .historial(List.of())
                .repartidor(null)
                .build();

        when(pedido.getId()).thenReturn(25L);
        when(pedidoRepository.findByTrackingToken("token-25")).thenReturn(Optional.of(pedido));
        when(historialRepository.findAllByPedidoIdOrderByFechaCambioAsc(25L)).thenReturn(historial);
        when(pedidoMapper.toTrackingResponse(pedido, historial)).thenReturn(expectedResponse);

        // Act
        PedidoTrackingResponse response = trackPedidoUseCase.execute("token-25");

        // Assert
        assertThat(response).isSameAs(expectedResponse);
        verify(pedidoRepository).findByTrackingToken("token-25");
        verify(historialRepository).findAllByPedidoIdOrderByFechaCambioAsc(25L);
        verify(pedidoMapper).toTrackingResponse(pedido, historial);
    }

    @Test
    @DisplayName("Should map tracking response when historial is empty")
    void should_map_tracking_response_when_historial_is_empty() {
        // Arrange
        Pedido pedido = org.mockito.Mockito.mock(Pedido.class);
        List<PedidoEstadoHistorial> historial = List.of();
        PedidoTrackingResponse expectedResponse = PedidoTrackingResponse.builder()
                .numeroPedido("PED-30")
                .estadoActual("confirmado")
                .codigoEntrega("ZXCV99")
                .historial(List.of())
                .repartidor(null)
                .build();

        when(pedido.getId()).thenReturn(30L);
        when(pedidoRepository.findByTrackingToken("token-30")).thenReturn(Optional.of(pedido));
        when(historialRepository.findAllByPedidoIdOrderByFechaCambioAsc(30L)).thenReturn(historial);
        when(pedidoMapper.toTrackingResponse(pedido, historial)).thenReturn(expectedResponse);

        // Act
        PedidoTrackingResponse response = trackPedidoUseCase.execute("token-30");

        // Assert
        assertThat(response).isSameAs(expectedResponse);
        verify(pedidoRepository).findByTrackingToken("token-30");
        verify(historialRepository).findAllByPedidoIdOrderByFechaCambioAsc(30L);
        verify(pedidoMapper).toTrackingResponse(pedido, historial);
    }
}