package com.migestion.logistics.application;

import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.logistics.domain.FirmaDigital;
import com.migestion.logistics.domain.FirmaDigitalRepository;
import com.migestion.logistics.dto.FirmaSessionResponse;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetFirmaSessionUseCase {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(5);

    private final FirmaDigitalRepository firmaDigitalRepository;
    private final EntregaRepository entregaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;

    public GetFirmaSessionUseCase(
            FirmaDigitalRepository firmaDigitalRepository,
            EntregaRepository entregaRepository,
            PedidoRepository pedidoRepository,
            UsuarioTenantRepository usuarioTenantRepository
    ) {
        this.firmaDigitalRepository = firmaDigitalRepository;
        this.entregaRepository = entregaRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioTenantRepository = usuarioTenantRepository;
    }

    @Transactional(readOnly = true)
    public FirmaSessionResponse execute(String token) {
        UUID tokenUnico = parseToken(token);

        FirmaDigital firmaDigital = firmaDigitalRepository.findByTokenUnico(tokenUnico)
                .orElseThrow(() -> new ResourceNotFoundException("FirmaDigital", token));

        Instant expiracion = firmaDigital.getGeneradoEn().plus(TOKEN_TTL);
        if (isExpiredOrUsed(firmaDigital, expiracion)) {
            throw new ResourceNotFoundException("Signature session is not available");
        }

        Entrega entrega = entregaRepository.findById(firmaDigital.getEntregaId())
                .orElseThrow(() -> new ResourceNotFoundException("Entrega", firmaDigital.getEntregaId()));

        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(entrega.getTenantId());

        try {
            Pedido pedido = pedidoRepository.findByIdAndTenantId(firmaDigital.getPedidoId(), entrega.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido", firmaDigital.getPedidoId()));

            UsuarioTenant repartidor = null;
            if (entrega.getRepartidorId() != null) {
                repartidor = usuarioTenantRepository.findByIdAndTenantId(entrega.getRepartidorId(), entrega.getTenantId())
                        .orElse(null);
            }

            return FirmaSessionResponse.builder()
                    .token(firmaDigital.getTokenUnico())
                    .estado("PENDIENTE")
                    .pedido(FirmaSessionResponse.PedidoInfo.builder()
                            .id(pedido.getId())
                            .numeroPedido(pedido.getNumeroPedido())
                            .total(pedido.getTotal())
                            .tipoEntrega(pedido.getTipoEntrega())
                            .metodoPago(pedido.getMetodoPago())
                            .fechaPedido(pedido.getFechaPedido())
                            .build())
                    .repartidor(FirmaSessionResponse.RepartidorInfo.builder()
                            .id(entrega.getRepartidorId())
                            .nombre(repartidor != null ? repartidor.getNombre() : null)
                            .apellido(repartidor != null ? repartidor.getApellido() : null)
                            .telefono(repartidor != null ? repartidor.getTelefono() : null)
                            .build())
                    .generadoEn(firmaDigital.getGeneradoEn())
                    .expiracion(expiracion)
                    .build();
        } finally {
            TenantContext.setTenantId(previousTenantId);
        }
    }

    private UUID parseToken(String token) {
        try {
            return UUID.fromString(token);
        } catch (IllegalArgumentException exception) {
            throw new ResourceNotFoundException("FirmaDigital", token);
        }
    }

    private boolean isExpiredOrUsed(FirmaDigital firmaDigital, Instant expiracion) {
        return firmaDigital.isExpirado()
                || firmaDigital.getFirmadoEn() != null
                || Instant.now().isAfter(expiracion);
    }
}