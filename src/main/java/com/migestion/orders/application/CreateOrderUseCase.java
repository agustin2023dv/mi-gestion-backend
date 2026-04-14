package com.migestion.orders.application;

import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.domain.ProductoRepository;
import com.migestion.orders.domain.EstadoPedido;
import com.migestion.orders.domain.EstadoPedidoRepository;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.orders.domain.event.OrderCreatedEvent;
import com.migestion.orders.dto.CreatePedidoRequest;
import com.migestion.orders.dto.ItemPedidoRequest;
import com.migestion.orders.dto.PedidoResponse;
import com.migestion.orders.infrastructure.mapper.PedidoMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CreateOrderUseCase {

    private static final DateTimeFormatter ORDER_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int MAX_ORDER_NUMBER_ATTEMPTS = 10;

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final ProductoRepository productoRepository;
    private final StockVerificationPort stockVerificationPort;
    private final StockReservationPort stockReservationPort;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PedidoMapper pedidoMapper;

    public CreateOrderUseCase(
            PedidoRepository pedidoRepository,
            PedidoItemRepository pedidoItemRepository,
            EstadoPedidoRepository estadoPedidoRepository,
            ProductoRepository productoRepository,
            StockVerificationPort stockVerificationPort,
            StockReservationPort stockReservationPort,
            ApplicationEventPublisher applicationEventPublisher,
            PedidoMapper pedidoMapper
    ) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.productoRepository = productoRepository;
        this.stockVerificationPort = stockVerificationPort;
        this.stockReservationPort = stockReservationPort;
        this.applicationEventPublisher = applicationEventPublisher;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional
    public PedidoResponse execute(CreatePedidoRequest request, Long tenantId) {
        validateInput(request, tenantId);

        Long previousTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);

        try {
            validateStock(request.items());

            EstadoPedido estadoInicial = resolveInitialOrderState();
            String trackingToken = UUID.randomUUID().toString();
            String numeroPedido = generateUniqueOrderNumber();

            List<PedidoItem> transientItems = buildTransientItems(request.items(), tenantId);
            BigDecimal subtotal = transientItems.stream()
                    .map(PedidoItem::calcularSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal impuestos = calculateImpuestos(subtotal);
            BigDecimal costoEnvio = calculateCostoEnvio(request);
            BigDecimal descuentoInicial = calculateDescuentoInicial(request, subtotal);
            BigDecimal descuentoAdicional = calculateDescuentoAdicional(request, subtotal);
            BigDecimal total = subtotal
                    .add(impuestos)
                    .add(costoEnvio)
                    .subtract(descuentoInicial)
                    .subtract(descuentoAdicional)
                    .setScale(2, RoundingMode.HALF_UP);

            if (total.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessRuleViolationException(
                        "ORDER_TOTAL_INVALID",
                        "The calculated order total cannot be negative"
                );
            }

            Pedido pedido = Pedido.builder()
                    .tenantId(tenantId)
                    .clienteId(null)
                    .direccionEntregaId(request.direccionEntregaId())
                    .estado(estadoInicial)
                    .tarifaDeliveryId(request.tarifaDeliveryId())
                    .numeroPedido(numeroPedido)
                    .tipoEntrega(request.tipoEntrega())
                    .subtotal(subtotal)
                    .impuestos(impuestos)
                    .costoEnvio(costoEnvio)
                    .descuentoInicial(descuentoInicial)
                    .descuentoAdicional(descuentoAdicional)
                    .total(total)
                    .estadoPago("pendiente")
                    .trackingToken(trackingToken)
                    .esProgramado(request.fechaEntregaSolicitada() != null)
                    .fechaEntregaSolicitada(request.fechaEntregaSolicitada())
                    .notasCliente(request.notasCliente())
                    .fechaPedido(Instant.now())
                    .build();

            Pedido savedPedido = pedidoRepository.save(pedido);
            List<PedidoItem> savedItems = persistItems(savedPedido.getId(), transientItems);

            reserveStock(request.items());

            applicationEventPublisher.publishEvent(
                    OrderCreatedEvent.builder()
                            .pedidoId(savedPedido.getId())
                            .tenantId(tenantId)
                            .numeroPedido(savedPedido.getNumeroPedido())
                            .trackingToken(savedPedido.getTrackingToken())
                            .total(savedPedido.getTotal())
                            .occurredAt(Instant.now())
                            .build()
            );

            return pedidoMapper.toResponse(savedPedido, savedItems);
        } finally {
            if (previousTenantId == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(previousTenantId);
            }
        }
    }

    private void validateInput(CreatePedidoRequest request, Long tenantId) {
        if (request == null) {
            throw new BusinessRuleViolationException("ORDER_REQUEST_REQUIRED", "CreatePedidoRequest is required");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessRuleViolationException("TENANT_ID_REQUIRED", "tenantId must be greater than 0");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessRuleViolationException("ORDER_ITEMS_REQUIRED", "At least one order item is required");
        }
        if (!StringUtils.hasText(request.tipoEntrega())) {
            throw new BusinessRuleViolationException("DELIVERY_TYPE_REQUIRED", "tipoEntrega is required");
        }
    }

    private void validateStock(List<ItemPedidoRequest> items) {
        for (ItemPedidoRequest item : items) {
            boolean hasSufficientStock = stockVerificationPort.hasSufficientStock(item.productoId(), item.cantidad());
            if (!hasSufficientStock) {
                throw new BusinessRuleViolationException(
                        "INSUFFICIENT_STOCK",
                        "Insufficient stock for product " + item.productoId()
                );
            }
        }
    }

    private void reserveStock(List<ItemPedidoRequest> items) {
        for (ItemPedidoRequest item : items) {
            stockReservationPort.reserveStock(item.productoId(), item.cantidad());
        }
    }

    private EstadoPedido resolveInitialOrderState() {
        List<String> possibleCodes = List.of("PENDING", "PENDIENTE", "pending", "pendiente");

        for (String code : possibleCodes) {
            EstadoPedido estado = estadoPedidoRepository.findByCodigo(code).orElse(null);
            if (estado != null) {
                return estado;
            }
        }

        throw new BusinessRuleViolationException(
                "ORDER_INITIAL_STATE_NOT_FOUND",
                "Could not find an initial order state (PENDING/PENDIENTE)"
        );
    }

    private List<PedidoItem> buildTransientItems(List<ItemPedidoRequest> requestItems, Long tenantId) {
        List<PedidoItem> items = new ArrayList<>();

        for (ItemPedidoRequest requestItem : requestItems) {
            Producto producto = productoRepository.findByIdAndTenantId(requestItem.productoId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", requestItem.productoId()));

            BigDecimal precioBase = defaultMoney(producto.getPrecio());
            BigDecimal precioExtras = BigDecimal.ZERO;
            BigDecimal costoUnitario = resolveCostoUnitario(producto);

            PedidoItem item = PedidoItem.builder()
                    .productoId(requestItem.productoId())
                    .cantidad(requestItem.cantidad())
                    .precioBaseSnapshot(precioBase)
                    .precioExtrasSnapshot(precioExtras)
                    .costoUnitarioSnapshot(costoUnitario)
                    .nombreProductoSnapshot(producto.getNombre())
                    .skuSnapshot(producto.getSku())
                    .build();

            items.add(item);
        }

        return items;
    }

    private List<PedidoItem> persistItems(Long pedidoId, List<PedidoItem> transientItems) {
        List<PedidoItem> itemsToPersist = new ArrayList<>();

        for (PedidoItem item : transientItems) {
            PedidoItem persistentItem = PedidoItem.builder()
                    .pedidoId(pedidoId)
                    .productoId(item.getProductoId())
                    .cantidad(item.getCantidad())
                    .precioBaseSnapshot(defaultMoney(item.getPrecioBaseSnapshot()))
                    .precioExtrasSnapshot(defaultMoney(item.getPrecioExtrasSnapshot()))
                    .costoUnitarioSnapshot(defaultMoney(item.getCostoUnitarioSnapshot()))
                    .nombreProductoSnapshot(item.getNombreProductoSnapshot())
                    .skuSnapshot(item.getSkuSnapshot())
                    .build();

            itemsToPersist.add(persistentItem);
        }

        return pedidoItemRepository.saveAll(itemsToPersist);
    }

    private BigDecimal resolveCostoUnitario(Producto producto) {
        if (!producto.isUsaCostoCalculado() && producto.getCostoUnitarioManualOverride() != null) {
            return defaultMoney(producto.getCostoUnitarioManualOverride());
        }
        return defaultMoney(producto.getCostoUnitarioCalculado());
    }

    private BigDecimal calculateImpuestos(BigDecimal subtotal) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCostoEnvio(CreatePedidoRequest request) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDescuentoInicial(CreatePedidoRequest request, BigDecimal subtotal) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDescuentoAdicional(CreatePedidoRequest request, BigDecimal subtotal) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultMoney(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateUniqueOrderNumber() {
        String datePart = LocalDate.now(ZoneOffset.UTC).format(ORDER_DATE_FORMAT);

        for (int attempt = 0; attempt < MAX_ORDER_NUMBER_ATTEMPTS; attempt++) {
            int sequence = ThreadLocalRandom.current().nextInt(0, 10000);
            String numeroPedido = String.format("PED-%s-%04d", datePart, sequence);
            if (!pedidoRepository.existsByNumeroPedido(numeroPedido)) {
                return numeroPedido;
            }
        }

        throw new BusinessRuleViolationException(
                "ORDER_NUMBER_GENERATION_FAILED",
                "Could not generate a unique order number"
        );
    }
}
