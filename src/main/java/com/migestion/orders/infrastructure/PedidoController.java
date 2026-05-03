package com.migestion.orders.infrastructure;

import com.migestion.orders.application.CreateOrderUseCase;
import com.migestion.orders.application.GetPedidoUseCase;
import com.migestion.orders.application.SearchPedidosUseCase;
import com.migestion.orders.application.TrackPedidoUseCase;
import com.migestion.orders.dto.CreatePedidoRequest;
import com.migestion.orders.dto.PedidoResponse;
import com.migestion.orders.dto.PedidoTrackingResponse;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.dto.PageResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.shared.security.TenantContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetPedidoUseCase getPedidoUseCase;
    private final TrackPedidoUseCase trackPedidoUseCase;
    private final SearchPedidosUseCase searchPedidosUseCase;

    public PedidoController(
            CreateOrderUseCase createOrderUseCase,
            GetPedidoUseCase getPedidoUseCase,
            TrackPedidoUseCase trackPedidoUseCase,
            SearchPedidosUseCase searchPedidosUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getPedidoUseCase = getPedidoUseCase;
        this.trackPedidoUseCase = trackPedidoUseCase;
        this.searchPedidosUseCase = searchPedidosUseCase;
    }

    /**
     * POST /api/v1/pedidos
     * Creates a new order. Supports both authenticated clients and guest checkout.
     * Tenant is resolved from TenantContext (populated by TenantFilter from subdomain or JWT).
     * Requires Idempotency-Key header; duplicate keys return the previously created order.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PedidoResponse>> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreatePedidoRequest request,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required to create an order. Provide a valid subdomain or Authorization header.");
        }

        PedidoResponse response = createOrderUseCase.execute(request, tenantId, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/pedidos
     * Lists orders for the current tenant with pagination and sorting.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PedidoResponse>>> list(
            Pageable pageable,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        if (user == null) {
            throw new AccessDeniedException("Authentication required to list orders");
        }

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }

        PageResponse<PedidoResponse> response = searchPedidosUseCase.execute(tenantId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // NOTE: Spring Security 7 dropped required=false on @AuthenticationPrincipal;
    // null is injected automatically for unauthenticated (guest) requests.

    /**
     * GET /api/v1/pedidos/{id}
     * Retrieves full details of a specific order.
     * Authenticated clients may only access their own orders; tenant roles may access any order in their tenant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }

        PedidoResponse response = getPedidoUseCase.execute(id, tenantId, user.getRole(), user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/pedidos/rastreos/{token}
     * Public endpoint to retrieve order tracking info by its tracking token.
     * Visibility is controlled by the tenant's visibilidadPublica setting (enforced by TenantFilter).
     */
    @GetMapping("/rastreos/{token}")
    public ResponseEntity<ApiResponse<PedidoTrackingResponse>> track(@PathVariable String token) {
        PedidoTrackingResponse response = trackPedidoUseCase.execute(token);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
