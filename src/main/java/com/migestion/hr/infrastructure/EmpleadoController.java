package com.migestion.hr.infrastructure;

import com.migestion.hr.application.CalculatePayrollUseCase;
import com.migestion.hr.application.CreateEmpleadoUseCase;
import com.migestion.hr.application.GetAllEmpleadosUseCase;
import com.migestion.hr.application.GetEmpleadoByIdUseCase;
import com.migestion.hr.dto.CalculatePayrollRequest;
import com.migestion.hr.dto.CreateEmpleadoRequest;
import com.migestion.hr.dto.EmpleadoResponse;
import com.migestion.hr.dto.NominaEmpleadoResponse;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {

    private final CreateEmpleadoUseCase createEmpleadoUseCase;
    private final GetAllEmpleadosUseCase getAllEmpleadosUseCase;
    private final GetEmpleadoByIdUseCase getEmpleadoByIdUseCase;
    private final CalculatePayrollUseCase calculatePayrollUseCase;

    public EmpleadoController(
            CreateEmpleadoUseCase createEmpleadoUseCase,
            GetAllEmpleadosUseCase getAllEmpleadosUseCase,
            GetEmpleadoByIdUseCase getEmpleadoByIdUseCase,
            CalculatePayrollUseCase calculatePayrollUseCase) {
        this.createEmpleadoUseCase = createEmpleadoUseCase;
        this.getAllEmpleadosUseCase = getAllEmpleadosUseCase;
        this.getEmpleadoByIdUseCase = getEmpleadoByIdUseCase;
        this.calculatePayrollUseCase = calculatePayrollUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpleadoResponse>>> getAllEmpleados(
            @RequestParam(required = false) Boolean activeOnly) {
        Long tenantId = requireTenantId();
        List<EmpleadoResponse> empleados = getAllEmpleadosUseCase.execute(tenantId, activeOnly);
        return ResponseEntity.ok(ApiResponse.success(empleados));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpleadoResponse>> getEmpleadoById(@PathVariable Long id) {
        Long tenantId = requireTenantId();
        EmpleadoResponse empleado = getEmpleadoByIdUseCase.execute(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(empleado));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmpleadoResponse>> createEmpleado(
            @Valid @RequestBody CreateEmpleadoRequest request) {
        Long tenantId = requireTenantId();
        EmpleadoResponse created = createEmpleadoUseCase.execute(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PostMapping("/nomina/calcular")
    public ResponseEntity<ApiResponse<NominaEmpleadoResponse>> calculatePayroll(
            @Valid @RequestBody CalculatePayrollRequest request) {
        Long tenantId = requireTenantId();
        NominaEmpleadoResponse nomina = calculatePayrollUseCase.execute(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(nomina));
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required"
            );
        }
        return tenantId;
    }
}
