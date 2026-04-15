package com.migestion.logistics.infrastructure;

import com.migestion.logistics.application.CalcularCostoEnvioUseCase;
import com.migestion.orders.application.DeliveryCostPort;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.util.GeolocationUtils;
import com.migestion.tenant.domain.ClienteRepository;
import com.migestion.tenant.domain.Direccion;
import com.migestion.tenant.domain.DireccionRepository;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalDeliveryCostAdapter implements DeliveryCostPort {

    private final CalcularCostoEnvioUseCase calcularCostoEnvioUseCase;
    private final TenantRepository tenantRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final DireccionRepository direccionRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public BigDecimal calculateDeliveryCost(Long tenantId, Long direccionEntregaId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessRuleViolationException("TENANT_ID_REQUIRED", "tenantId must be greater than 0");
        }
        if (direccionEntregaId == null || direccionEntregaId <= 0) {
            throw new BusinessRuleViolationException(
                    "DELIVERY_ADDRESS_REQUIRED",
                    "direccionEntregaId must be greater than 0"
            );
        }

        Coordinates origen = resolveTenantBaseCoordinates(tenantId);
        Coordinates destino = resolveDeliveryCoordinates(tenantId, direccionEntregaId);

        double distanciaCalculada = GeolocationUtils.calcularDistanciaKm(
                origen.latitud().doubleValue(),
                origen.longitud().doubleValue(),
                destino.latitud().doubleValue(),
                destino.longitud().doubleValue()
        );

        BigDecimal distanciaKm = BigDecimal.valueOf(Math.max(distanciaCalculada, 0.01d))
                .setScale(4, RoundingMode.HALF_UP);

        return calcularCostoEnvioUseCase.execute(tenantId, distanciaKm);
    }

    private Coordinates resolveTenantBaseCoordinates(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));

        if (tenant.getPropietarioId() == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_BASE_ADDRESS_NOT_CONFIGURED",
                    "Tenant owner is not configured"
            );
        }

        UsuarioTenant propietario = usuarioTenantRepository
                .findByIdAndTenantId(tenant.getPropietarioId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioTenant", tenant.getPropietarioId()));

        if (propietario.getDireccionId() == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_BASE_ADDRESS_NOT_CONFIGURED",
                    "Tenant base address is not configured"
            );
        }

        Direccion direccionBase = direccionRepository.findById(propietario.getDireccionId())
                .orElseThrow(() -> new ResourceNotFoundException("Direccion", propietario.getDireccionId()));

        return toCoordinates(direccionBase, "TENANT_BASE_COORDINATES_REQUIRED");
    }

    private Coordinates resolveDeliveryCoordinates(Long tenantId, Long direccionEntregaId) {
        Direccion direccionEntrega = direccionRepository.findById(direccionEntregaId)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion", direccionEntregaId));

        if (direccionEntrega.getClienteId() == null
                || clienteRepository.findByIdAndTenantId(direccionEntrega.getClienteId(), tenantId).isEmpty()) {
            throw new BusinessRuleViolationException(
                    "DELIVERY_ADDRESS_NOT_BELONG_TO_TENANT",
                    "Delivery address does not belong to the current tenant"
            );
        }

        return toCoordinates(direccionEntrega, "DELIVERY_COORDINATES_REQUIRED");
    }

    private Coordinates toCoordinates(Direccion direccion, String errorCode) {
        if (direccion.getLatitud() == null || direccion.getLongitud() == null) {
            throw new BusinessRuleViolationException(
                    errorCode,
                    "Address coordinates are required to calculate delivery cost"
            );
        }
        return new Coordinates(direccion.getLatitud(), direccion.getLongitud());
    }

    private record Coordinates(BigDecimal latitud, BigDecimal longitud) {
    }
}