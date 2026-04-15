package com.migestion.marketing.application;

import com.migestion.marketing.domain.Cupon;
import com.migestion.marketing.domain.CuponRepository;
import com.migestion.marketing.dto.CuponResponse;
import com.migestion.marketing.dto.UpdateCuponRequest;
import com.migestion.marketing.infrastructure.mapper.CuponMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
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
public class UpdateCuponUseCase {

    private final CuponRepository cuponRepository;
    private final CuponMapper cuponMapper;

    public CuponResponse execute(Long cuponId, UpdateCuponRequest request) {
        Long tenantId = requireTenantId();
        Cupon cupon = cuponRepository.findByIdAndTenantId(cuponId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupon", cuponId));

        if (request.codigo() != null) {
            String codigoNormalizado = normalizeCodigo(request.codigo());
            cuponRepository.findByCodigoAndTenantId(codigoNormalizado, tenantId)
                    .filter(existing -> !existing.getId().equals(cuponId))
                    .ifPresent(existing -> {
                        throw new BusinessRuleViolationException("CODIGO_DUPLICADO", "A coupon with this code already exists");
                    });
            cupon.setCodigo(codigoNormalizado);
        }

        if (request.tipoDescuento() != null) {
            cupon.setTipoDescuento(normalizeTipoDescuento(request.tipoDescuento()));
        }

        if (request.valorDescuento() != null) {
            cupon.setValorDescuento(request.valorDescuento());
        }

        if (request.usosMaximos() != null) {
            if (request.usosMaximos() < cupon.getUsosActuales()) {
                throw new BusinessRuleViolationException(
                        "USOS_MAXIMOS_INVALIDO",
                        "usosMaximos cannot be less than current usages"
                );
            }
            cupon.setUsosMaximos(request.usosMaximos());
        }

        if (request.fechaInicio() != null) {
            cupon.setFechaInicio(request.fechaInicio());
        }
        if (request.fechaFin() != null) {
            cupon.setFechaFin(request.fechaFin());
        }
        if (request.montoMinimo() != null) {
            cupon.setMontoMinimo(request.montoMinimo());
        }
        if (request.isActive() != null) {
            cupon.setActive(request.isActive());
        }

        validateDiscountRules(cupon.getTipoDescuento(), cupon.getValorDescuento());
        validateDateWindow(cupon.getFechaInicio(), cupon.getFechaFin());

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
}
