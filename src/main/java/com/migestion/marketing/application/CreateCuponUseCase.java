package com.migestion.marketing.application;

import com.migestion.marketing.domain.Cupon;
import com.migestion.marketing.domain.CuponRepository;
import com.migestion.marketing.dto.CreateCuponRequest;
import com.migestion.marketing.dto.CuponResponse;
import com.migestion.marketing.infrastructure.mapper.CuponMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCuponUseCase {

    private final CuponRepository cuponRepository;
    private final CuponMapper cuponMapper;

    public CuponResponse execute(CreateCuponRequest request) {
        Long tenantId = requireTenantId();
        String codigoNormalizado = normalizeCodigo(request.codigo());
        String tipoDescuentoNormalizado = normalizeTipoDescuento(request.tipoDescuento());

        if (cuponRepository.existsByCodigoAndTenantId(codigoNormalizado, tenantId)) {
            throw new BusinessRuleViolationException("CODIGO_DUPLICADO", "A coupon with this code already exists");
        }

        validateDiscountRules(tipoDescuentoNormalizado, request.valorDescuento());
        validateDateWindow(request.fechaInicio(), request.fechaFin());

        Cupon cupon = cuponMapper.toEntity(request);
        cupon.setTenantId(tenantId);
        cupon.setCodigo(codigoNormalizado);
        cupon.setTipoDescuento(tipoDescuentoNormalizado);
        cupon.setMontoMinimo(defaultMontoMinimo(request.montoMinimo()));
        cupon.setUsosActuales(0);
        cupon.setActive(request.isActive() == null || request.isActive());

        Cupon persisted = cuponRepository.save(cupon);
        return cuponMapper.toResponse(persisted);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }

    private void validateDiscountRules(String tipoDescuento, BigDecimal valorDescuento) {
        if ("porcentaje".equals(tipoDescuento)
                && (valorDescuento.compareTo(BigDecimal.ZERO) <= 0 || valorDescuento.compareTo(new BigDecimal("100")) > 0)) {
            throw new BusinessRuleViolationException(
                    "VALOR_DESCUENTO_INVALIDO",
                    "Percentage discounts must be greater than 0 and less than or equal to 100"
            );
        }
        if ("monto_fijo".equals(tipoDescuento) && valorDescuento.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException(
                    "VALOR_DESCUENTO_INVALIDO",
                    "Fixed amount discounts must be greater than 0"
            );
        }
    }

    private void validateDateWindow(Instant fechaInicio, Instant fechaFin) {
        if (fechaInicio.isBefore(Instant.now())) {
            throw new BusinessRuleViolationException("FECHA_INICIO_INVALIDA", "Start date cannot be in the past");
        }
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new BusinessRuleViolationException("FECHA_FIN_ANTERIOR_INICIO", "End date must be after start date");
        }
    }

    private String normalizeCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase();
    }

    private String normalizeTipoDescuento(String tipoDescuento) {
        if (!StringUtils.hasText(tipoDescuento)) {
            throw new BusinessRuleViolationException("TIPO_DESCUENTO_INVALIDO", "tipoDescuento is required");
        }
        String normalized = tipoDescuento.trim().toLowerCase();
        if (!"porcentaje".equals(normalized) && !"monto_fijo".equals(normalized)) {
            throw new BusinessRuleViolationException(
                    "TIPO_DESCUENTO_INVALIDO",
                    "tipoDescuento must be 'porcentaje' or 'monto_fijo'"
            );
        }
        return normalized;
    }

    private BigDecimal defaultMontoMinimo(BigDecimal montoMinimo) {
        return montoMinimo == null ? BigDecimal.ZERO : montoMinimo;
    }
}
