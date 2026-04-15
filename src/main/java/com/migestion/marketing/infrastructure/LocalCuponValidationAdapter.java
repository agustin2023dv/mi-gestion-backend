package com.migestion.marketing.infrastructure;

import com.migestion.marketing.application.ValidateCuponUseCase;
import com.migestion.marketing.domain.CuponRepository;
import com.migestion.marketing.domain.PedidoCupon;
import com.migestion.marketing.domain.PedidoCuponRepository;
import com.migestion.marketing.dto.ValidateCuponRequest;
import com.migestion.marketing.dto.ValidateCuponResponse;
import com.migestion.orders.application.CuponValidationPort;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalCuponValidationAdapter implements CuponValidationPort {

    private final ValidateCuponUseCase validateCuponUseCase;
    private final CuponRepository cuponRepository;
    private final PedidoCuponRepository pedidoCuponRepository;

    @Override
    public CuponValidationResult validateCoupon(String codigo, BigDecimal montoPedido) {
        ValidateCuponResponse validation = validateCuponUseCase.execute(
                ValidateCuponRequest.builder()
                        .codigo(codigo)
                        .montoPedido(defaultMoney(montoPedido))
                        .build()
        );

        if (!validation.valido() || validation.cupon() == null) {
            String motivo = validation.motivo() == null ? "CUPON_INVALIDO" : validation.motivo();
            throw new BusinessRuleViolationException(motivo, "Coupon code is invalid or not applicable");
        }

        return CuponValidationResult.builder()
                .cuponId(validation.cupon().id())
                .codigo(validation.cupon().codigo())
                .montoDescuento(defaultMoney(validation.montoDescuento()))
                .build();
    }

    @Override
    public void registerCouponUsage(Long pedidoId, Long cuponId, BigDecimal montoDescuento) {
        Long tenantId = requireTenantContext();

        int affectedRows = cuponRepository.incrementUsosActualesIfAvailable(cuponId, tenantId, Instant.now());
        if (affectedRows != 1) {
            throw new BusinessRuleViolationException(
                    "USOS_AGOTADOS",
                    "Coupon usage limit reached before order could be completed"
            );
        }

        pedidoCuponRepository.save(PedidoCupon.builder()
                .pedidoId(pedidoId)
                .cuponId(cuponId)
                .montoDescuento(defaultMoney(montoDescuento))
                .aplicadoEn(Instant.now())
                .build());
    }

    private Long requireTenantContext() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException("TENANT_CONTEXT_REQUIRED", "Tenant context is required");
        }
        return tenantId;
    }

    private BigDecimal defaultMoney(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}