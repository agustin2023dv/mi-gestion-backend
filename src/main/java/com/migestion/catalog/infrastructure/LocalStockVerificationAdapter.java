package com.migestion.catalog.infrastructure;

import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.domain.ProductoRepository;
import com.migestion.orders.application.StockVerificationPort;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import org.springframework.stereotype.Service;

@Service
public class LocalStockVerificationAdapter implements StockVerificationPort {

    private final ProductoRepository productoRepository;

    public LocalStockVerificationAdapter(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public boolean hasSufficientStock(Long productId, Integer quantity) {
        validateInput(productId, quantity);

        Long tenantId = requireTenantContext();
        Producto producto = productoRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));

        int availableStock = defaultInteger(producto.getStock()) - defaultInteger(producto.getStockReservado());
        return availableStock >= quantity;
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