package com.migestion.finance.infrastructure;

import com.migestion.analytics.domain.SimuladorEscenario;
import com.migestion.finance.application.DeleteEscenarioUseCase;
import com.migestion.finance.application.GetEscenariosGuardadosUseCase;
import com.migestion.finance.application.GuardarEscenarioUseCase;
import com.migestion.finance.application.SimularEscenarioUseCase;
import com.migestion.finance.dto.GuardarEscenarioRequest;
import com.migestion.finance.dto.SimulacionResultadoResponse;
import com.migestion.finance.dto.SimuladorCalculoRequest;
import com.migestion.finance.dto.SimuladorEscenarioResponse;
import com.migestion.finance.infrastructure.mapper.FinanceMapper;
import com.migestion.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for financial simulator scenarios.
 *
 * Base path: /api/v1/simulador
 * Requires FINANZAS_CONFIGURAR permission.
 */
@RestController
@RequestMapping("/api/v1/simulador")
@PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
public class SimuladorController {

    private final SimularEscenarioUseCase simularEscenarioUseCase;
    private final GuardarEscenarioUseCase guardarEscenarioUseCase;
    private final GetEscenariosGuardadosUseCase getEscenariosGuardadosUseCase;
    private final DeleteEscenarioUseCase deleteEscenarioUseCase;
    private final FinanceMapper financeMapper;

    public SimuladorController(
            SimularEscenarioUseCase simularEscenarioUseCase,
            GuardarEscenarioUseCase guardarEscenarioUseCase,
            GetEscenariosGuardadosUseCase getEscenariosGuardadosUseCase,
            DeleteEscenarioUseCase deleteEscenarioUseCase,
            FinanceMapper financeMapper
    ) {
        this.simularEscenarioUseCase = simularEscenarioUseCase;
        this.guardarEscenarioUseCase = guardarEscenarioUseCase;
        this.getEscenariosGuardadosUseCase = getEscenariosGuardadosUseCase;
        this.deleteEscenarioUseCase = deleteEscenarioUseCase;
        this.financeMapper = financeMapper;
    }

    /**
     * POST /api/v1/simulador/calcular
     * Calculates a simulation preview without persisting a scenario.
     */
    @PostMapping("/calcular")
    public ResponseEntity<ApiResponse<SimulacionResultadoResponse>> calcular(
            @Valid @RequestBody SimuladorCalculoRequest request
    ) {
        SimularEscenarioUseCase.ResultadoSimulacion resultado = simularEscenarioUseCase.execute(
                request.precioPromedioSimulado(),
                request.costoVariableSimulado(),
                request.costosEmpleadosSimulado(),
                request.costosIngredientesSimulado(),
                request.gastosFijosSimulado()
        );

        SimulacionResultadoResponse response = SimulacionResultadoResponse.builder()
                .precioPromedioSimulado(resultado.precioPromedioSimulado())
                .costoVariableSimulado(resultado.costoVariableSimulado())
                .costosEmpleadosSimulado(resultado.costosEmpleadosSimulado())
                .costosIngredientesSimulado(resultado.costosIngredientesSimulado())
                .gastosFijosSimulado(resultado.gastosFijosSimulado())
                .margenSimulado(resultado.margenSimulado())
                .bepSimulado(resultado.bepSimulado())
                .ingresosNecesarios(resultado.ingresosNecesarios())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * POST /api/v1/simulador
     * Saves a simulation scenario.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SimuladorEscenarioResponse>> guardar(
            @Valid @RequestBody GuardarEscenarioRequest request
    ) {
        SimuladorEscenario escenario = guardarEscenarioUseCase.execute(
                request.nombreEscenario(),
                request.precioPromedioSimulado(),
                request.costoVariableSimulado(),
                request.costosEmpleadosSimulado(),
                request.costosIngredientesSimulado(),
                request.gastosFijosSimulado()
        );

        SimuladorEscenarioResponse response = financeMapper.toSimuladorEscenarioResponse(escenario);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/simulador
     * Lists all saved scenarios for the current tenant.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SimuladorEscenarioResponse>>> listAll() {
        List<SimuladorEscenarioResponse> escenarios = getEscenariosGuardadosUseCase.execute().stream()
                .map(financeMapper::toSimuladorEscenarioResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(escenarios));
    }

    /**
     * DELETE /api/v1/simulador/{id}
     * Deletes a saved scenario.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deleteEscenarioUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
