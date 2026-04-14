package com.migestion.catalog.application;

import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.domain.ProductoRepository;
import com.migestion.catalog.dto.CreateProductoRequest;
import com.migestion.catalog.dto.ProductoResponse;
import com.migestion.catalog.infrastructure.ProductoMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CreateProductoUseCase {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public CreateProductoUseCase(ProductoRepository productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    @Transactional
    public ProductoResponse execute(CreateProductoRequest request) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required to create a product"
            );
        }

        validateBusinessRules(request, tenantId);

        Producto producto = productoMapper.toEntity(request);
        producto.setTenantId(tenantId);
        producto.setPrecio(request.precio());
        producto.setCostoUnitarioCalculado(BigDecimal.ZERO);
        producto.setUsaCostoCalculado(Boolean.TRUE.equals(request.usaCostoCalculado()) || request.usaCostoCalculado() == null);
        producto.setStock(defaultInteger(request.stock()));
        producto.setStockMinimo(defaultInteger(request.stockMinimo()));
        producto.setEsPersonalizable(Boolean.TRUE.equals(request.esPersonalizable()));
        producto.setEsServicio(Boolean.TRUE.equals(request.esServicio()));
        producto.setPermiteBooking(Boolean.TRUE.equals(request.permiteBooking()));
        producto.setBufferEntreTurnosMin(defaultInteger(request.bufferEntreTurnosMin()));
        producto.setRequiereVerificacionEdad(Boolean.TRUE.equals(request.requiereVerificacionEdad()));
        producto.setRequiereEmpleadoEspecifico(Boolean.TRUE.equals(request.requiereEmpleadoEspecifico()));
        producto.setActive(request.isActive() == null || request.isActive());
        producto.setSku(normalizeSku(request.sku()));

        if (producto.isUsaCostoCalculado()) {
            producto.setCostoUnitarioManualOverride(null);
        }

        if (!producto.isEsServicio()) {
            producto.setDuracionMinutos(null);
            producto.setPermiteBooking(false);
            producto.setBufferEntreTurnosMin(0);
            producto.setRequiereEmpleadoEspecifico(false);
        }

        if (!producto.isRequiereVerificacionEdad()) {
            producto.setEdadMinima(null);
        }

        Producto persistedProducto = productoRepository.save(producto);
        return productoMapper.toResponse(persistedProducto);
    }

    private void validateBusinessRules(CreateProductoRequest request, Long tenantId) {
        String normalizedSku = normalizeSku(request.sku());
        if (StringUtils.hasText(normalizedSku) && productoRepository.existsByTenantIdAndSku(tenantId, normalizedSku)) {
            throw new BusinessRuleViolationException(
                    "PRODUCTO_SKU_DUPLICATE",
                    "A product with the same SKU already exists for this tenant"
            );
        }

        if (Boolean.TRUE.equals(request.permiteBooking()) && !Boolean.TRUE.equals(request.esServicio())) {
            throw new BusinessRuleViolationException(
                    "PRODUCTO_BOOKING_REQUIRES_SERVICE",
                    "A product can allow booking only when it is a service"
            );
        }

        if (Boolean.TRUE.equals(request.esServicio()) && request.duracionMinutos() == null) {
            throw new BusinessRuleViolationException(
                    "PRODUCTO_SERVICE_DURATION_REQUIRED",
                    "Service products require duracionMinutos"
            );
        }

        if (Boolean.FALSE.equals(request.usaCostoCalculado()) && request.costoUnitarioManualOverride() == null) {
            throw new BusinessRuleViolationException(
                    "PRODUCTO_MANUAL_COST_REQUIRED",
                    "costoUnitarioManualOverride is required when usaCostoCalculado is false"
            );
        }
    }

    private Integer defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private String normalizeSku(String sku) {
        if (!StringUtils.hasText(sku)) {
            return null;
        }
        return sku.trim();
    }
}