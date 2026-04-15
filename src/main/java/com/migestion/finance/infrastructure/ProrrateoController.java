package com.migestion.finance.infrastructure;

import com.migestion.finance.application.CalcularProrrateoUseCase;
import com.migestion.finance.application.EjecutarProrrateoUseCase;
import com.migestion.finance.application.GetHistorialProrrateoUseCase;
import com.migestion.finance.domain.AsignacionCostoIndirecto;
import com.migestion.finance.dto.AsignacionCostoIndirectoResponse;
import com.migestion.finance.dto.HistorialProrrateoResponse;
import com.migestion.finance.dto.ProrrateoRequest;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.dto.PageResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for proration execution and history.
 *
 * Base path: /api/v1/prorrateo
 * Requires FINANZAS_CONFIGURAR permission.
 */
@RestController
@RequestMapping("/api/v1/prorrateo")
@PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
public class ProrrateoController {

    private final CalcularProrrateoUseCase calcularProrrateoUseCase;
    private final EjecutarProrrateoUseCase ejecutarProrrateoUseCase;
    private final GetHistorialProrrateoUseCase getHistorialProrrateoUseCase;
    private final FinanceMapper financeMapper;

    public ProrrateoController(
            CalcularProrrateoUseCase calcularProrrateoUseCase,
            EjecutarProrrateoUseCase ejecutarProrrateoUseCase,
            GetHistorialProrrateoUseCase getHistorialProrrateoUseCase,
            FinanceMapper financeMapper) {
        this.calcularProrrateoUseCase = calcularProrrateoUseCase;
        this.ejecutarProrrateoUseCase = ejecutarProrrateoUseCase;
        this.getHistorialProrrateoUseCase = getHistorialProrrateoUseCase;
        this.financeMapper = financeMapper;
    }

    /**
     * POST /api/v1/prorrateo/calcular
     * Calculates a proration preview without persisting assignments.
     */
    @PostMapping("/calcular")
    public ResponseEntity<ApiResponse<List<AsignacionCostoIndirectoResponse>>> calcular(
            @Valid @RequestBody ProrrateoRequest request) {
        List<AsignacionCostoIndirecto> asignaciones = calcularProrrateoUseCase.execute(
                request.periodoInicio(),
                request.periodoFin(),
                request.criterioId()
        );

        return ResponseEntity.ok(ApiResponse.success(toResponse(asignaciones)));
    }

    /**
     * POST /api/v1/prorrateo/ejecutar
     * Executes and persists proration assignments for the given period.
     */
    @PostMapping("/ejecutar")
    public ResponseEntity<ApiResponse<List<AsignacionCostoIndirectoResponse>>> ejecutar(
            @Valid @RequestBody ProrrateoRequest request) {
        List<AsignacionCostoIndirecto> asignaciones = ejecutarProrrateoUseCase.execute(
                request.periodoInicio(),
                request.periodoFin(),
                request.criterioId()
        );

        return ResponseEntity.ok(ApiResponse.success(toResponse(asignaciones)));
    }

    /**
     * POST /api/v1/prorrateo/recalcular
     * Re-runs and persists proration assignments for the given period.
     */
    @PostMapping("/recalcular")
    public ResponseEntity<ApiResponse<List<AsignacionCostoIndirectoResponse>>> recalcular(
            @Valid @RequestBody ProrrateoRequest request) {
        List<AsignacionCostoIndirecto> asignaciones = ejecutarProrrateoUseCase.execute(
                request.periodoInicio(),
                request.periodoFin(),
                request.criterioId()
        );

        return ResponseEntity.ok(ApiResponse.success(toResponse(asignaciones)));
    }

    /**
     * GET /api/v1/prorrateo/historial
     * Returns paginated history of proration runs.
     */
    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<PageResponse<HistorialProrrateoResponse>>> historial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        validatePaginationParams(page, size, 100);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HistorialProrrateoResponse> historial = getHistorialProrrateoUseCase.execute(pageable);
        return ResponseEntity.ok(ApiResponse.success(historial));
    }

    private List<AsignacionCostoIndirectoResponse> toResponse(List<AsignacionCostoIndirecto> asignaciones) {
        return asignaciones.stream()
                .map(financeMapper::toAsignacionCostoIndirectoResponse)
                .toList();
    }

    private void validatePaginationParams(int page, int size, int maxSize) {
        if (page < 0) {
            throw new BusinessRuleViolationException(
                    "INVALID_PAGINATION_PAGE",
                    "Page index must be greater than or equal to zero"
            );
        }

        if (size <= 0 || size > maxSize) {
            throw new BusinessRuleViolationException(
                    "INVALID_PAGINATION_SIZE",
                    "Page size must be between 1 and " + maxSize
            );
        }
    }
}
