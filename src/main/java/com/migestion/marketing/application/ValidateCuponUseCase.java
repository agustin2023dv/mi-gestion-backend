package com.migestion.marketing.application;

import com.migestion.marketing.domain.Cupon;
import com.migestion.marketing.domain.CuponRepository;
import com.migestion.marketing.dto.ValidateCuponRequest;
import com.migestion.marketing.dto.ValidateCuponResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValidateCuponUseCase {

    private final CuponRepository cuponRepository;

    public ValidateCuponResponse execute(ValidateCuponRequest request) {
        Long tenantId = requireTenantId();
        String codigoNormalizado = normalizeCodigo(request.codigo());

        Cupon cupon = cuponRepository.findByCodigoAndTenantId(codigoNormalizado, tenantId)
                .orElse(null);

        if (cupon == null) {
            return invalid("CUPON_NO_ENCONTRADO");
        }
        if (!cupon.isActive()) {
            return invalid("CUPON_INACTIVO");
        }

        Instant now = Instant.now();
        if (cupon.getFechaInicio() != null && now.isBefore(cupon.getFechaInicio())) {
            return invalid("CUPON_EXPIRADO");
        }
        if (cupon.getFechaFin() != null && now.isAfter(cupon.getFechaFin())) {
            return invalid("CUPON_EXPIRADO");
        }

        if (cupon.getUsosMaximos() != null && cupon.getUsosActuales() >= cupon.getUsosMaximos()) {
            return invalid("USOS_AGOTADOS");
        }

        BigDecimal montoMinimo = cupon.getMontoMinimo() == null ? BigDecimal.ZERO : cupon.getMontoMinimo();
        if (request.montoPedido().compareTo(montoMinimo) < 0) {
            return invalid("MONTO_MINIMO_NO_ALCANZADO");
        }

        BigDecimal montoDescuento = calculateDiscount(cupon, request.montoPedido());

        return ValidateCuponResponse.builder()
                .valido(true)
                .cupon(ValidateCuponResponse.CuponValidationData.builder()
                        .id(cupon.getId())
                        .codigo(cupon.getCodigo())
                        .tipoDescuento(cupon.getTipoDescuento())
                        .valorDescuento(cupon.getValorDescuento())
                        .build())
                .montoDescuento(montoDescuento)
                .motivo(null)
                .build();
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }

    private ValidateCuponResponse invalid(String motivo) {
        return ValidateCuponResponse.builder()
                .valido(false)
                .cupon(null)
                .montoDescuento(BigDecimal.ZERO)
                .motivo(motivo)
                .build();
    }

    private BigDecimal calculateDiscount(Cupon cupon, BigDecimal montoPedido) {
        if ("porcentaje".equals(cupon.getTipoDescuento())) {
            return montoPedido
                    .multiply(cupon.getValorDescuento())
                    .divide(new BigDecimal("100"));
        }
        if ("monto_fijo".equals(cupon.getTipoDescuento())) {
            return cupon.getValorDescuento().min(montoPedido);
        }
        throw new BusinessRuleViolationException("TIPO_DESCUENTO_INVALIDO", "Unsupported discount type");
    }

    private String normalizeCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase();
    }
}
