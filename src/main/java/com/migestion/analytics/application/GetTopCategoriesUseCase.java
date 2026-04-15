package com.migestion.analytics.application;

import com.migestion.analytics.dto.CategoriaTopResponse;
import com.migestion.catalog.domain.Categoria;
import com.migestion.catalog.domain.CategoriaRepository;
import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.domain.ProductoRepository;
import com.migestion.catalog.domain.Subcategoria;
import com.migestion.catalog.domain.SubcategoriaRepository;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetTopCategoriesUseCase {

    private static final int DEFAULT_LIMIT = 10;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ProductoRepository productoRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final CategoriaRepository categoriaRepository;

    public GetTopCategoriesUseCase(
            PedidoRepository pedidoRepository,
            PedidoItemRepository pedidoItemRepository,
            ProductoRepository productoRepository,
            SubcategoriaRepository subcategoriaRepository,
            CategoriaRepository categoriaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.productoRepository = productoRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaTopResponse> execute(
            Long tenantId,
            String periodo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer limite) {
        AnalyticsTenantGuard.requireTenantAccess(tenantId);

        AnalyticsPeriodResolver.ResolvedPeriod resolvedPeriod = AnalyticsPeriodResolver.resolve(periodo, fechaInicio, fechaFin);
        List<Pedido> pedidos = pedidoRepository.findAllByTenantIdAndFechaPedidoGreaterThanEqualAndFechaPedidoLessThan(
                tenantId,
                resolvedPeriod.start(),
                resolvedPeriod.endExclusive());

        List<Long> pedidoIds = pedidos.stream().map(Pedido::getId).toList();
        if (pedidoIds.isEmpty()) {
            return List.of();
        }

        List<PedidoItem> items = pedidoItemRepository.findAllByPedidoIdIn(pedidoIds);
        Map<Long, Long> productToCategory = resolveProductCategories(items, tenantId);
        Map<Long, String> categoryNames = resolveCategoryNames(productToCategory.values());

        Map<Long, BigDecimal> revenueByCategory = new HashMap<>();
        for (PedidoItem item : items) {
            Long categoryId = productToCategory.get(item.getProductoId());
            if (categoryId == null) {
                continue;
            }
            revenueByCategory.merge(categoryId, calculateItemRevenue(item), BigDecimal::add);
        }

        BigDecimal totalRevenue = revenueByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        int safeLimit = limite == null || limite <= 0 ? DEFAULT_LIMIT : limite;

        return revenueByCategory.entrySet().stream()
                .map(entry -> CategoriaTopResponse.builder()
                        .categoriaId(entry.getKey())
                        .nombre(categoryNames.getOrDefault(entry.getKey(), "Sin categoria"))
                        .ingresosGenerados(entry.getValue())
                        .porcentaje(calculatePercentage(entry.getValue(), totalRevenue))
                        .build())
                .sorted(Comparator.comparing(CategoriaTopResponse::ingresosGenerados).reversed())
                .limit(safeLimit)
                .toList();
    }

    private Map<Long, Long> resolveProductCategories(List<PedidoItem> items, Long tenantId) {
        Set<Long> productIds = new HashSet<>();
        for (PedidoItem item : items) {
            if (item.getProductoId() != null) {
                productIds.add(item.getProductoId());
            }
        }

        Map<Long, Long> productToSubcategory = new HashMap<>();
        for (Long productId : productIds) {
            Optional<Producto> product = productoRepository.findByIdAndTenantId(productId, tenantId);
            product.ifPresent(value -> productToSubcategory.put(productId, value.getSubcategoriaId()));
        }

        Set<Long> subcategoryIds = productToSubcategory.values().stream()
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toSet());

        Map<Long, Long> subcategoryToCategory = subcategoriaRepository.findAllById(subcategoryIds).stream()
                .collect(java.util.stream.Collectors.toMap(Subcategoria::getId, subcategoria -> subcategoria.getCategoria().getId()));

        Map<Long, Long> productToCategory = new HashMap<>();
        for (Map.Entry<Long, Long> entry : productToSubcategory.entrySet()) {
            Long categoryId = subcategoryToCategory.get(entry.getValue());
            if (categoryId != null) {
                productToCategory.put(entry.getKey(), categoryId);
            }
        }

        return productToCategory;
    }

    private Map<Long, String> resolveCategoryNames(java.util.Collection<Long> categoryIds) {
        return categoriaRepository.findAllById(categoryIds).stream()
                .collect(java.util.stream.Collectors.toMap(Categoria::getId, Categoria::getNombre));
    }

    private BigDecimal calculateItemRevenue(PedidoItem item) {
        BigDecimal subtotal = item.getSubtotal();
        if (subtotal != null) {
            return subtotal;
        }
        return item.calcularSubtotal();
    }

    private BigDecimal calculatePercentage(BigDecimal value, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.multiply(ONE_HUNDRED).divide(total, 2, RoundingMode.HALF_UP);
    }
}