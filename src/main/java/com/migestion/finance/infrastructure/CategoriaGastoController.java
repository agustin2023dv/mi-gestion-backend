package com.migestion.finance.infrastructure;

import com.migestion.finance.application.CreateCategoriaGastoUseCase;
import com.migestion.finance.application.DeleteCategoriaGastoUseCase;
import com.migestion.finance.application.GetAllCategoriasGastoUseCase;
import com.migestion.finance.application.UpdateCategoriaGastoUseCase;
import com.migestion.finance.domain.CategoriaGasto;
import com.migestion.finance.domain.CategoriaGastoRepository;
import com.migestion.finance.dto.CategoriaGastoRequest;
import com.migestion.finance.dto.CategoriaGastoResponse;
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
 * REST controller for Expense Category (Categoria de Gasto) management.
 * Provides CRUD endpoints for expense categories used to classify operational costs.
 *
 * Base path: /api/v1/categorias-gasto
 * Requires FINANZAS_CONFIGURAR permission for write operations.
 * Requires FINANZAS_VIEW permission for read operations.
 */
@RestController
@RequestMapping("/api/v1/categorias-gasto")
public class CategoriaGastoController {

    private final CreateCategoriaGastoUseCase createCategoriaGastoUseCase;
    private final GetAllCategoriasGastoUseCase getAllCategoriasGastoUseCase;
    private final UpdateCategoriaGastoUseCase updateCategoriaGastoUseCase;
    private final DeleteCategoriaGastoUseCase deleteCategoriaGastoUseCase;
    private final CategoriaGastoRepository categoriaGastoRepository;
    private final FinanceMapper financeMapper;

    public CategoriaGastoController(
            CreateCategoriaGastoUseCase createCategoriaGastoUseCase,
            GetAllCategoriasGastoUseCase getAllCategoriasGastoUseCase,
            UpdateCategoriaGastoUseCase updateCategoriaGastoUseCase,
            DeleteCategoriaGastoUseCase deleteCategoriaGastoUseCase,
            CategoriaGastoRepository categoriaGastoRepository,
            FinanceMapper financeMapper) {
        this.createCategoriaGastoUseCase = createCategoriaGastoUseCase;
        this.getAllCategoriasGastoUseCase = getAllCategoriasGastoUseCase;
        this.updateCategoriaGastoUseCase = updateCategoriaGastoUseCase;
        this.deleteCategoriaGastoUseCase = deleteCategoriaGastoUseCase;
        this.categoriaGastoRepository = categoriaGastoRepository;
        this.financeMapper = financeMapper;
    }

    /**
     * GET /api/v1/categorias-gasto
     * Retrieves all expense categories for the current tenant.
     * Requires FINANZAS_VIEW permission.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('FINANZAS_VIEW')")
    public ResponseEntity<ApiResponse<List<CategoriaGastoResponse>>> listAll() {
        List<CategoriaGastoResponse> categorias = getAllCategoriasGastoUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(categorias));
    }

    /**
     * GET /api/v1/categorias-gasto/{id}
     * Retrieves a specific expense category by ID.
     * Requires FINANZAS_VIEW permission.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_VIEW')")
    public ResponseEntity<ApiResponse<CategoriaGastoResponse>> getById(@PathVariable Long id) {
        Long tenantId = requireTenantContext();
        CategoriaGasto categoriaGasto = categoriaGastoRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaGasto", id));
        return ResponseEntity.ok(ApiResponse.success(financeMapper.toCategoriaGastoResponse(categoriaGasto)));
    }

    /**
     * POST /api/v1/categorias-gasto
     * Creates a new expense category.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CategoriaGastoResponse>> create(
            @Valid @RequestBody CategoriaGastoRequest request) {
        CategoriaGastoResponse response = createCategoriaGastoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * PUT /api/v1/categorias-gasto/{id}
     * Fully updates an existing expense category.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CategoriaGastoResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaGastoRequest request) {
        CategoriaGastoResponse response = updateCategoriaGastoUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * PATCH /api/v1/categorias-gasto/{id}
     * Partially updates an existing expense category.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CategoriaGastoResponse>> partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaGastoRequest request) {
        CategoriaGastoResponse response = updateCategoriaGastoUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * DELETE /api/v1/categorias-gasto/{id}
     * Deletes (soft-deletes) an expense category.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteCategoriaGastoUseCase.execute(id);
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
