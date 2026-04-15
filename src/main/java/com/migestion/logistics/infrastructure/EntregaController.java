package com.migestion.logistics.infrastructure;

import com.migestion.logistics.application.ActualizarEstadoEntregaUseCase;
import com.migestion.logistics.application.AsignarRepartidorUseCase;
import com.migestion.logistics.application.GetComprobanteUseCase;
import com.migestion.logistics.application.GetEntregaByIdUseCase;
import com.migestion.logistics.application.GetEntregasPendientesUseCase;
import com.migestion.logistics.application.ListEntregasTenantUseCase;
import com.migestion.logistics.dto.ActualizarEstadoEntregaRequest;
import com.migestion.logistics.dto.AsignarRepartidorRequest;
import com.migestion.logistics.dto.ComprobanteResponse;
import com.migestion.logistics.dto.EntregaResponse;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.dto.PageResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.shared.security.TenantContext;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/entregas")
public class EntregaController {

    private final GetEntregasPendientesUseCase getEntregasPendientesUseCase;
    private final GetEntregaByIdUseCase getEntregaByIdUseCase;
    private final ActualizarEstadoEntregaUseCase actualizarEstadoEntregaUseCase;
    private final AsignarRepartidorUseCase asignarRepartidorUseCase;
    private final GetComprobanteUseCase getComprobanteUseCase;
    private final ListEntregasTenantUseCase listEntregasTenantUseCase;

    public EntregaController(
            GetEntregasPendientesUseCase getEntregasPendientesUseCase,
            GetEntregaByIdUseCase getEntregaByIdUseCase,
            ActualizarEstadoEntregaUseCase actualizarEstadoEntregaUseCase,
            AsignarRepartidorUseCase asignarRepartidorUseCase,
            GetComprobanteUseCase getComprobanteUseCase,
            ListEntregasTenantUseCase listEntregasTenantUseCase) {
        this.getEntregasPendientesUseCase = getEntregasPendientesUseCase;
        this.getEntregaByIdUseCase = getEntregaByIdUseCase;
        this.actualizarEstadoEntregaUseCase = actualizarEstadoEntregaUseCase;
        this.asignarRepartidorUseCase = asignarRepartidorUseCase;
        this.getComprobanteUseCase = getComprobanteUseCase;
        this.listEntregasTenantUseCase = listEntregasTenantUseCase;
    }

    /**
     * GET /api/v1/entregas/repartidor/actuales
     * Returns deliveries currently assigned to the authenticated courier
     * that are not yet completed (status ASIGNADA, EN_CAMINO).
     * Requires ENTREGA_VIEW permission.
     */
    @GetMapping("/repartidor/actuales")
    @PreAuthorize("hasAuthority('ENTREGA_VIEW')")
    public ResponseEntity<ApiResponse<PageResponse<EntregaResponse>>> getEntregasPendientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        validatePaginationParams(page, size, 50);

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }

        List<EntregaResponse> allEntregas = getEntregasPendientesUseCase.execute(user.getId(), tenantId);
        PageResponse<EntregaResponse> pageResponse = paginateList(allEntregas, page, size);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * GET /api/v1/entregas/{id}
     * Retrieves full details of a specific delivery.
     * Couriers may only access deliveries assigned to them.
     * TenantAdmin may access any.
     * Requires ENTREGA_VIEW permission.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ENTREGA_VIEW')")
    public ResponseEntity<ApiResponse<EntregaResponse>> getEntregaById(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }

        EntregaResponse entrega = getEntregaByIdUseCase.execute(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(entrega));
    }

    /**
     * PATCH /api/v1/entregas/{id}/estado
     * Updates the status of a delivery.
     * Requires ENTREGA_UPDATE_STATUS permission.
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ENTREGA_UPDATE_STATUS')")
    public ResponseEntity<ApiResponse<EntregaResponse>> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoEntregaRequest request,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }

        EntregaResponse entrega = actualizarEstadoEntregaUseCase.execute(id, request, tenantId);
        return ResponseEntity.ok(ApiResponse.success(entrega));
    }

    /**
     * POST /api/v1/entregas/{id}/asignar
     * Assigns a courier to a delivery.
     * Requires ENTREGA_ASIGNAR permission (TenantAdmin only).
     */
    @PostMapping("/{id}/asignar")
    @PreAuthorize("hasAuthority('ENTREGA_ASIGNAR')")
    public ResponseEntity<ApiResponse<EntregaResponse>> asignarRepartidor(
            @PathVariable Long id,
            @Valid @RequestBody AsignarRepartidorRequest request,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }

        EntregaResponse entrega = asignarRepartidorUseCase.execute(id, request, tenantId);
        return ResponseEntity.ok(ApiResponse.success(entrega));
    }

    /**
     * GET /api/v1/entregas/{id}/comprobante
     * Returns the delivery receipt PDF URL.
     * Couriers may access for their completed deliveries.
     * TenantAdmin may access any.
     * Requires ENTREGA_VIEW permission.
     */
    @GetMapping("/{id}/comprobante")
    @PreAuthorize("hasAuthority('ENTREGA_VIEW')")
    public ResponseEntity<ApiResponse<ComprobanteResponse>> getComprobante(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }

        ComprobanteResponse comprobante = getComprobanteUseCase.execute(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(comprobante));
    }

    /**
     * GET /api/v1/entregas/tenant
     * Lists all deliveries in the current tenant with optional filtering.
     * Requires ENTREGA_VIEW permission (TenantAdmin, TenantManager).
     */
    @GetMapping("/tenant")
    @PreAuthorize("hasAuthority('ENTREGA_VIEW')")
    public ResponseEntity<ApiResponse<PageResponse<EntregaResponse>>> listEntregasTenant(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long repartidorId,
            @AuthenticationPrincipal AuthenticatedUserDetails user) {

        validatePaginationParams(page, size, 100);

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }

        List<EntregaResponse> allEntregas = listEntregasTenantUseCase.execute(tenantId, estado, repartidorId);
        PageResponse<EntregaResponse> pageResponse = paginateList(allEntregas, page, size);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Helper method to validate pagination parameters.
     */
    private void validatePaginationParams(int page, int size, int maxSize) {
        if (page < 0) {
            throw new BusinessRuleViolationException("INVALID_PAGE_NUMBER", "page must be greater than or equal to 0");
        }
        if (size < 1 || size > maxSize) {
            throw new BusinessRuleViolationException(
                    "INVALID_PAGE_SIZE",
                    "size must be between 1 and " + maxSize);
        }
    }

    /**
     * Helper method to paginate a list of items.
     */
    private <T> PageResponse<T> paginateList(List<T> items, int page, int size) {
        long totalElements = items.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, items.size());

        List<T> pageContent = items.subList(startIndex, endIndex);

        return PageResponse.<T>builder()
                .content(pageContent)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .build();
    }
}
