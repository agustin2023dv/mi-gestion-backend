package com.migestion.catalog.infrastructure;

import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.domain.ProductoRepository;
import com.migestion.orders.application.StockReservationPort;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalStockReservationAdapter implements StockReservationPort {

    private final ProductoRepository productoRepository;

    public LocalStockReservationAdapter(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        validateInput(productId, quantity);

        Long tenantId = requireTenantContext();
        Producto producto = productoRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        int currentStock = defaultInteger(producto.getStock());
        int currentReservedStock = defaultInteger(producto.getStockReservado());
        int availableStock = currentStock - currentReservedStock;

        if (availableStock < quantity) {
            throw new BusinessRuleViolationException(
                    "INSUFFICIENT_STOCK",
                    "Insufficient stock to reserve the requested quantity"
            );
        }

        producto.setStockReservado(currentReservedStock + quantity);
        productoRepository.save(producto);
    }

    private void validateInput(Long productId, Integer quantity) {
        if (productId == null) {
            throw new BusinessRuleViolationException("PRODUCT_ID_REQUIRED", "productId is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessRuleViolationException("INVALID_QUANTITY", "quantity must be greater than 0");
        }
    }

    private Long requireTenantContext() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required for stock operations"
            );
        }
        return tenantId;
    }

    private Integer defaultInteger(Integer value) {
        return value == null ? 0 : value;
    }
}