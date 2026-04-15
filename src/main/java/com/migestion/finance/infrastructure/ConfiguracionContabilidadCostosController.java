package com.migestion.finance.infrastructure;

import com.migestion.finance.application.GetConfiguracionContabilidadCostosUseCase;
import com.migestion.finance.application.UpdateConfiguracionContabilidadCostosUseCase;
import com.migestion.finance.dto.ConfiguracionContabilidadCostosRequest;
import com.migestion.finance.dto.ConfiguracionContabilidadCostosResponse;
import com.migestion.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contabilidad-costos")
@PreAuthorize("hasAuthority('FINANZAS_CONFIGURAR')")
public class ConfiguracionContabilidadCostosController {

    private final GetConfiguracionContabilidadCostosUseCase getConfiguracionContabilidadCostosUseCase;
    private final UpdateConfiguracionContabilidadCostosUseCase updateConfiguracionContabilidadCostosUseCase;

    public ConfiguracionContabilidadCostosController(
            GetConfiguracionContabilidadCostosUseCase getConfiguracionContabilidadCostosUseCase,
            UpdateConfiguracionContabilidadCostosUseCase updateConfiguracionContabilidadCostosUseCase
    ) {
        this.getConfiguracionContabilidadCostosUseCase = getConfiguracionContabilidadCostosUseCase;
        this.updateConfiguracionContabilidadCostosUseCase = updateConfiguracionContabilidadCostosUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ConfiguracionContabilidadCostosResponse>> getConfiguracion() {
        ConfiguracionContabilidadCostosResponse response = getConfiguracionContabilidadCostosUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ConfiguracionContabilidadCostosResponse>> updateConfiguracion(
            @Valid @RequestBody ConfiguracionContabilidadCostosRequest request
    ) {
        ConfiguracionContabilidadCostosResponse response = updateConfiguracionContabilidadCostosUseCase.executePut(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<ConfiguracionContabilidadCostosResponse>> partialUpdateConfiguracion(
            @Valid @RequestBody ConfiguracionContabilidadCostosRequest request
    ) {
        ConfiguracionContabilidadCostosResponse response = updateConfiguracionContabilidadCostosUseCase.executePatch(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
