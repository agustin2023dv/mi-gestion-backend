package com.migestion.orders.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.dto.PedidoResponse;
import com.migestion.orders.infrastructure.mapper.PedidoMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPedidoUseCase")
class GetPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private PedidoItemRepository pedidoItemRepository;
    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private GetPedidoUseCase getPedidoUseCase;

    @Test
    @DisplayName("Should return pedido for CLIENTE role using tenant and cliente filters")
    void should_return_pedido_for_cliente_role_using_tenant_and_cliente_filters() {
        // Arrange
        Long pedidoId = 8L;
        Long tenantId = 100L;
        Long clienteId = 200L;

        Pedido pedido = Pedido.builder()
                .tenantId(tenantId)
                .clienteId(clienteId)
                .numeroPedido("PED-100-0008")
                .total(new BigDecimal("32.00"))
                .build();

        List<PedidoItem> items = List.of(PedidoItem.builder().pedidoId(pedidoId).cantidad(1).build());

        PedidoResponse expected = PedidoResponse.builder()
                .id(pedidoId)
                .tenantId(tenantId)
                .clienteId(clienteId)
                .numeroPedido("PED-100-0008")
                .total(new BigDecimal("32.00"))
                .build();

        when(pedidoRepository.findByIdAndClienteIdAndTenantId(pedidoId, clienteId, tenantId))
                .thenReturn(Optional.of(pedido));
        when(pedidoItemRepository.findAllByPedidoId(pedido.getId())).thenReturn(items);
        when(pedidoMapper.toResponse(pedido, items)).thenReturn(expected);

        // Act
        PedidoResponse response = getPedidoUseCase.execute(pedidoId, tenantId, "CLIENTE", clienteId);

        // Assert
        assertThat(response.id()).isEqualTo(pedidoId);
        assertThat(response.tenantId()).isEqualTo(tenantId);
        verify(pedidoRepository).findByIdAndClienteIdAndTenantId(pedidoId, clienteId, tenantId);
        verify(pedidoRepository, never()).findByIdAndTenantId(pedidoId, tenantId);
    }

    @Test
    @DisplayName("Should prevent tenant leakage for CLIENTE role when pedido is outside tenant scope")
    void should_prevent_tenant_leakage_for_cliente_role_when_pedido_is_outside_tenant_scope() {
        // Arrange
        Long pedidoId = 8L;
        Long tenantId = 100L;
        Long clienteId = 200L;

        when(pedidoRepository.findByIdAndClienteIdAndTenantId(pedidoId, clienteId, tenantId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> getPedidoUseCase.execute(pedidoId, tenantId, "CLIENTE", clienteId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pedido");

        verify(pedidoRepository).findByIdAndClienteIdAndTenantId(pedidoId, clienteId, tenantId);
        verify(pedidoItemRepository, never()).findAllByPedidoId(pedidoId);
    }

    @Test
    @DisplayName("Should return pedido for non-CLIENTE role using tenant filter only")
    void should_return_pedido_for_non_cliente_role_using_tenant_filter_only() {
        // Arrange
        Long pedidoId = 9L;
        Long tenantId = 300L;

        Pedido pedido = Pedido.builder()
                .tenantId(tenantId)
                .numeroPedido("PED-300-0009")
                .build();

        List<PedidoItem> items = List.of();
        PedidoResponse expected = PedidoResponse.builder()
                .id(pedidoId)
                .tenantId(tenantId)
                .numeroPedido("PED-300-0009")
                .build();

        when(pedidoRepository.findByIdAndTenantId(pedidoId, tenantId)).thenReturn(Optional.of(pedido));
        when(pedidoItemRepository.findAllByPedidoId(pedido.getId())).thenReturn(items);
        when(pedidoMapper.toResponse(pedido, items)).thenReturn(expected);

        // Act
        PedidoResponse response = getPedidoUseCase.execute(pedidoId, tenantId, "TENANT_ADMIN", 999L);

        // Assert
        assertThat(response.id()).isEqualTo(pedidoId);
        verify(pedidoRepository).findByIdAndTenantId(pedidoId, tenantId);
        verify(pedidoRepository, never()).findByIdAndClienteIdAndTenantId(pedidoId, 999L, tenantId);
    }
}
