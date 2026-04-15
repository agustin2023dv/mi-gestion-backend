package com.migestion.finance.infrastructure;

import com.migestion.finance.application.CreateGastoOperativoUseCase;
import com.migestion.finance.application.DeleteGastoOperativoUseCase;
import com.migestion.finance.application.GetAllCategoriasGastoUseCase;
import com.migestion.finance.application.GetAllCentrosCostoUseCase;
import com.migestion.finance.application.GetAllGastosOperativosUseCase;
import com.migestion.finance.application.GetGastoOperativoByIdUseCase;
import com.migestion.finance.application.UpdateGastoOperativoUseCase;
import com.migestion.finance.dto.CategoriaGastoResponse;
import com.migestion.finance.dto.CentroCostoResponse;
import com.migestion.finance.dto.CreateGastoOperativoRequest;
import com.migestion.finance.dto.GastoOperativoFilterRequest;
import com.migestion.finance.dto.GastoOperativoResponse;
import com.migestion.finance.dto.UpdateGastoOperativoRequest;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.dto.PageResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Operating Expense (Gasto Operativo) management.
 * Provides CRUD endpoints plus supporting lookup endpoints for categories and cost centres.
 *
 * Base path: /api/v1/gastos
 */
@RestController
@RequestMapping("/api/v1/gastos")
public class GastoOperativoController {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of("fechaRegistro", "monto", "nombre");

    private final CreateGastoOperativoUseCase createGastoOperativoUseCase;
    private final GetAllGastosOperativosUseCase getAllGastosOperativosUseCase;
    private final GetGastoOperativoByIdUseCase getGastoOperativoByIdUseCase;
    private final UpdateGastoOperativoUseCase updateGastoOperativoUseCase;
    private final DeleteGastoOperativoUseCase deleteGastoOperativoUseCase;
    private final GetAllCategoriasGastoUseCase getAllCategoriasGastoUseCase;
    private final GetAllCentrosCostoUseCase getAllCentrosCostoUseCase;

    public GastoOperativoController(
            CreateGastoOperativoUseCase createGastoOperativoUseCase,
            GetAllGastosOperativosUseCase getAllGastosOperativosUseCase,
            GetGastoOperativoByIdUseCase getGastoOperativoByIdUseCase,
            UpdateGastoOperativoUseCase updateGastoOperativoUseCase,
            DeleteGastoOperativoUseCase deleteGastoOperativoUseCase,
            GetAllCategoriasGastoUseCase getAllCategoriasGastoUseCase,
            GetAllCentrosCostoUseCase getAllCentrosCostoUseCase) {
        this.createGastoOperativoUseCase = createGastoOperativoUseCase;
        this.getAllGastosOperativosUseCase = getAllGastosOperativosUseCase;
        this.getGastoOperativoByIdUseCase = getGastoOperativoByIdUseCase;
        this.updateGastoOperativoUseCase = updateGastoOperativoUseCase;
        this.deleteGastoOperativoUseCase = deleteGastoOperativoUseCase;
        this.getAllCategoriasGastoUseCase = getAllCategoriasGastoUseCase;
        this.getAllCentrosCostoUseCase = getAllCentrosCostoUseCase;
    }

    /**
     * GET /api/v1/gastos
     * Returns paginated expenses with optional filters.
     * Requires GASTO_VIEW permission.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('GASTO_VIEW')")
    public ResponseEntity<ApiResponse<PageResponse<GastoOperativoResponse>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fechaRegistro,desc") String sort,
            @RequestParam(required = false) Instant fechaDesde,
            @RequestParam(required = false) Instant fechaHasta,
            @RequestParam(required = false) Long categoriaGastoId,
            @RequestParam(required = false) Long centroCostoId,
            @RequestParam(required = false) Boolean esProrrateable) {

        validatePaginationParams(page, size, 100);
        Pageable pageable = buildPageable(page, size, sort);

        GastoOperativoFilterRequest filter = GastoOperativoFilterRequest.builder()
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .categoriaGastoId(categoriaGastoId)
                .centroCostoId(centroCostoId)
                .esProrrateable(esProrrateable)
                .build();

        PageResponse<GastoOperativoResponse> response = getAllGastosOperativosUseCase.execute(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/gastos/{id}
     * Returns a single expense by ID.
     * Requires GASTO_VIEW permission.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GASTO_VIEW')")
    public ResponseEntity<ApiResponse<GastoOperativoResponse>> getById(@PathVariable Long id) {
        GastoOperativoResponse response = getGastoOperativoByIdUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * POST /api/v1/gastos
     * Creates a new expense.
     * Requires GASTO_CREATE permission.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('GASTO_CREATE')")
    public ResponseEntity<ApiResponse<GastoOperativoResponse>> create(
            @Valid @RequestBody CreateGastoOperativoRequest request) {
        GastoOperativoResponse response = createGastoOperativoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * PUT /api/v1/gastos/{id}
     * Fully updates an existing expense.
     * Requires GASTO_UPDATE permission.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GASTO_UPDATE')")
    public ResponseEntity<ApiResponse<GastoOperativoResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGastoOperativoRequest request) {
        GastoOperativoResponse response = updateGastoOperativoUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * PATCH /api/v1/gastos/{id}
     * Partially updates an existing expense.
     * Requires GASTO_UPDATE permission.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('GASTO_UPDATE')")
    public ResponseEntity<ApiResponse<GastoOperativoResponse>> partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGastoOperativoRequest request) {
        GastoOperativoResponse response = updateGastoOperativoUseCase.execute(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * DELETE /api/v1/gastos/{id}
     * Soft-deletes an expense.
     * Requires GASTO_DELETE permission.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GASTO_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteGastoOperativoUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * GET /api/v1/gastos/categorias
     * Returns all expense categories for the current tenant.
     * Requires GASTO_VIEW permission.
     */
    @GetMapping("/categorias")
    @PreAuthorize("hasAuthority('GASTO_VIEW')")
    public ResponseEntity<ApiResponse<List<CategoriaGastoResponse>>> listCategorias() {
        List<CategoriaGastoResponse> categorias = getAllCategoriasGastoUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(categorias));
    }

    /**
     * GET /api/v1/gastos/centros-costo
     * Returns all cost centres for the current tenant.
     * Requires GASTO_VIEW permission.
     */
    @GetMapping("/centros-costo")
    @PreAuthorize("hasAuthority('GASTO_VIEW')")
    public ResponseEntity<ApiResponse<List<CentroCostoResponse>>> listCentrosCosto() {
        List<CentroCostoResponse> centrosCosto = getAllCentrosCostoUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(centrosCosto));
    }

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

    private Pageable buildPageable(int page, int size, String sortParam) {
        String[] parts = Arrays.stream(sortParam.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        if (parts.length == 0 || parts[0].isBlank()) {
            throw new BusinessRuleViolationException("INVALID_SORT", "sort field is required");
        }

        String field = parts[0];
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            throw new BusinessRuleViolationException(
                    "INVALID_SORT_FIELD",
                    "Allowed sort fields: " + String.join(", ", ALLOWED_SORT_FIELDS));
        }

        Sort.Direction direction = Sort.Direction.DESC;
        if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1])) {
            direction = Sort.Direction.ASC;
        }

        return PageRequest.of(page, size, Sort.by(direction, field));
    }
}
