package com.migestion.finance.infrastructure;

import com.migestion.finance.application.CreateCentroCostoUseCase;
import com.migestion.finance.application.DeleteCentroCostoUseCase;
import com.migestion.finance.application.GetAllCentrosCostoUseCase;
import com.migestion.finance.application.UpdateCentroCostoUseCase;
import com.migestion.finance.domain.CentroCosto;
import com.migestion.finance.domain.CentroCostoRepository;
import com.migestion.finance.dto.CentroCostoRequest;
import com.migestion.finance.dto.CentroCostoResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.TenantContext;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Cost Centre (Centro de Costo) management.
 * Provides CRUD endpoints for cost centres used to classify expenses and labour.
 *
 * Base path: /api/v1/centros-costo
 * Requires FINANZAS_CONFIGURAR permission for write operations.
 * Requires FINANZAS_VIEW permission for read operations.
 */
@RestController
@RequestMapping("/api/v1/centros-costo")
public class CentroCostoController {

    private final CreateCentroCostoUseCase createCentroCostoUseCase;
    private final GetAllCentrosCostoUseCase getAllCentrosCostoUseCase;
    private final UpdateCentroCostoUseCase updateCentroCostoUseCase;
    private final DeleteCentroCostoUseCase deleteCentroCostoUseCase;
    private final CentroCostoRepository centroCostoRepository;
    private final FinanceMapper financeMapper;

    public CentroCostoController(
            CreateCentroCostoUseCase createCentroCostoUseCase,
            GetAllCentrosCostoUseCase getAllCentrosCostoUseCase,
            UpdateCentroCostoUseCase updateCentroCostoUseCase,
            DeleteCentroCostoUseCase deleteCentroCostoUseCase,
            CentroCostoRepository centroCostoRepository,
            FinanceMapper financeMapper) {
        this.createCentroCostoUseCase = createCentroCostoUseCase;
        this.getAllCentrosCostoUseCase = getAllCentrosCostoUseCase;
        this.updateCentroCostoUseCase = updateCentroCostoUseCase;
        this.deleteCentroCostoUseCase = deleteCentroCostoUseCase;
        this.centroCostoRepository = centroCostoRepository;
        this.financeMapper = financeMapper;
    }

    /**
     * GET /api/v1/centros-costo
     * Retrieves all cost centres for the current tenant.
     * Requires FINANZAS_VIEW permission.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('FINANZAS_VIEW')")
    public ResponseEntity<ApiResponse<List<CentroCostoResponse>>> listAll() {
        List<CentroCostoResponse> centrosCosto = getAllCentrosCostoUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(centrosCosto));
    }

    /**
     * GET /api/v1/centros-costo/{id}
     * Retrieves a specific cost centre by ID.
     * Requires FINANZAS_VIEW permission.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_VIEW')")
    public ResponseEntity<ApiResponse<CentroCostoResponse>> getById(@PathVariable Long id) {
        Long tenantId = requireTenantContext();
        CentroCosto centroCosto = centroCostoRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CentroCosto", id));
        return ResponseEntity.ok(ApiResponse.success(financeMapper.toCentroCostoResponse(centroCosto)));
    }

    /**
     * POST /api/v1/centros-costo
     * Creates a new cost centre.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CentroCostoResponse>> create(
            @Valid @RequestBody CentroCostoRequest request) {
        CentroCostoResponse response = createCentroCostoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * PUT /api/v1/centros-costo/{id}
     * Fully updates an existing cost centre.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CentroCostoResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CentroCostoRequest request) {
        CentroCostoResponse response = updateCentroCostoUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * PATCH /api/v1/centros-costo/{id}
     * Partially updates an existing cost centre.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CentroCostoResponse>> partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody CentroCostoRequest request) {
        CentroCostoResponse response = updateCentroCostoUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * DELETE /api/v1/centros-costo/{id}
     * Deletes (soft-deletes) a cost centre.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteCentroCostoUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    private Long requireTenantContext() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required for financial operations"
            );
        }
        return tenantId;
    }
}
