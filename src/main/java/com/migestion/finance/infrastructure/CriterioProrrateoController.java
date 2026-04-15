package com.migestion.finance.infrastructure;

import com.migestion.finance.application.CreateCriterioProrrateoUseCase;
import com.migestion.finance.application.DeleteCriterioProrrateoUseCase;
import com.migestion.finance.application.GetAllCriteriosProrrateoUseCase;
import com.migestion.finance.application.UpdateCriterioProrrateoUseCase;
import com.migestion.finance.domain.CriterioProrrateo;
import com.migestion.finance.domain.CriterioProrrateoRepository;
import com.migestion.finance.dto.CriterioProrrateoRequest;
import com.migestion.finance.dto.CriterioProrrateoResponse;
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
 * REST controller for Proration Criteria (Criterio de Prorrateo) management.
 * Provides CRUD endpoints for proration criteria used to allocate indirect costs.
 *
 * Base path: /api/v1/criterios-prorrateo
 * Requires FINANZAS_CONFIGURAR permission for write operations.
 * Requires FINANZAS_VIEW permission for read operations.
 */
@RestController
@RequestMapping("/api/v1/criterios-prorrateo")
public class CriterioProrrateoController {

    private final CreateCriterioProrrateoUseCase createCriterioProrrateoUseCase;
    private final GetAllCriteriosProrrateoUseCase getAllCriteriosProrrateoUseCase;
    private final UpdateCriterioProrrateoUseCase updateCriterioProrrateoUseCase;
    private final DeleteCriterioProrrateoUseCase deleteCriterioProrrateoUseCase;
    private final CriterioProrrateoRepository criterioProrrateoRepository;
    private final FinanceMapper financeMapper;

    public CriterioProrrateoController(
            CreateCriterioProrrateoUseCase createCriterioProrrateoUseCase,
            GetAllCriteriosProrrateoUseCase getAllCriteriosProrrateoUseCase,
            UpdateCriterioProrrateoUseCase updateCriterioProrrateoUseCase,
            DeleteCriterioProrrateoUseCase deleteCriterioProrrateoUseCase,
            CriterioProrrateoRepository criterioProrrateoRepository,
            FinanceMapper financeMapper) {
        this.createCriterioProrrateoUseCase = createCriterioProrrateoUseCase;
        this.getAllCriteriosProrrateoUseCase = getAllCriteriosProrrateoUseCase;
        this.updateCriterioProrrateoUseCase = updateCriterioProrrateoUseCase;
        this.deleteCriterioProrrateoUseCase = deleteCriterioProrrateoUseCase;
        this.criterioProrrateoRepository = criterioProrrateoRepository;
        this.financeMapper = financeMapper;
    }

    /**
     * GET /api/v1/criterios-prorrateo
     * Retrieves all proration criteria for the current tenant.
     * Requires FINANZAS_VIEW permission.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('FINANZAS_VIEW')")
    public ResponseEntity<ApiResponse<List<CriterioProrrateoResponse>>> listAll() {
        List<CriterioProrrateoResponse> criterios = getAllCriteriosProrrateoUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(criterios));
    }

    /**
     * GET /api/v1/criterios-prorrateo/{id}
     * Retrieves a specific proration criterion by ID.
     * Requires FINANZAS_VIEW permission.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_VIEW')")
    public ResponseEntity<ApiResponse<CriterioProrrateoResponse>> getById(@PathVariable Long id) {
        Long tenantId = requireTenantContext();
        CriterioProrrateo criterioProrrateo = criterioProrrateoRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CriterioProrrateo", id));
        return ResponseEntity.ok(ApiResponse.success(financeMapper.toCriterioProrrateoResponse(criterioProrrateo)));
    }

    /**
     * POST /api/v1/criterios-prorrateo
     * Creates a new proration criterion.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CriterioProrrateoResponse>> create(
            @Valid @RequestBody CriterioProrrateoRequest request) {
        CriterioProrrateoResponse response = createCriterioProrrateoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * PUT /api/v1/criterios-prorrateo/{id}
     * Fully updates an existing proration criterion.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CriterioProrrateoResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CriterioProrrateoRequest request) {
        CriterioProrrateoResponse response = updateCriterioProrrateoUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * PATCH /api/v1/criterios-prorrateo/{id}
     * Partially updates an existing proration criterion.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<CriterioProrrateoResponse>> partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody CriterioProrrateoRequest request) {
        CriterioProrrateoResponse response = updateCriterioProrrateoUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * DELETE /api/v1/criterios-prorrateo/{id}
     * Deletes (soft-deletes) a proration criterion.
     * Requires FINANZAS_CONFIGURAR permission.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteCriterioProrrateoUseCase.execute(id);
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
