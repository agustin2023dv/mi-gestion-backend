package com.migestion.logistics.infrastructure;

import com.migestion.logistics.application.CalcularCostoEnvioUseCase;
import com.migestion.logistics.application.CreateTarifaDeliveryUseCase;
import com.migestion.logistics.application.DeleteTarifaDeliveryUseCase;
import com.migestion.logistics.application.GetAllTarifasDeliveryUseCase;
import com.migestion.logistics.application.GetTarifaDeliveryByIdUseCase;
import com.migestion.logistics.application.PatchTarifaDeliveryUseCase;
import com.migestion.logistics.application.UpdateTarifaDeliveryUseCase;
import com.migestion.logistics.dto.TarifaDeliveryPatchRequest;
import com.migestion.logistics.dto.TarifaDeliveryRequest;
import com.migestion.logistics.dto.TarifaDeliveryResponse;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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

@Validated
@RestController
@RequestMapping("/api/v1/tarifas-delivery")
public class TarifaDeliveryController {

    private final GetAllTarifasDeliveryUseCase getAllTarifasDeliveryUseCase;
    private final GetTarifaDeliveryByIdUseCase getTarifaDeliveryByIdUseCase;
    private final CreateTarifaDeliveryUseCase createTarifaDeliveryUseCase;
    private final UpdateTarifaDeliveryUseCase updateTarifaDeliveryUseCase;
    private final PatchTarifaDeliveryUseCase patchTarifaDeliveryUseCase;
    private final DeleteTarifaDeliveryUseCase deleteTarifaDeliveryUseCase;
    private final CalcularCostoEnvioUseCase calcularCostoEnvioUseCase;

    public TarifaDeliveryController(
            GetAllTarifasDeliveryUseCase getAllTarifasDeliveryUseCase,
            GetTarifaDeliveryByIdUseCase getTarifaDeliveryByIdUseCase,
            CreateTarifaDeliveryUseCase createTarifaDeliveryUseCase,
            UpdateTarifaDeliveryUseCase updateTarifaDeliveryUseCase,
            PatchTarifaDeliveryUseCase patchTarifaDeliveryUseCase,
            DeleteTarifaDeliveryUseCase deleteTarifaDeliveryUseCase,
            CalcularCostoEnvioUseCase calcularCostoEnvioUseCase
    ) {
        this.getAllTarifasDeliveryUseCase = getAllTarifasDeliveryUseCase;
        this.getTarifaDeliveryByIdUseCase = getTarifaDeliveryByIdUseCase;
        this.createTarifaDeliveryUseCase = createTarifaDeliveryUseCase;
        this.updateTarifaDeliveryUseCase = updateTarifaDeliveryUseCase;
        this.patchTarifaDeliveryUseCase = patchTarifaDeliveryUseCase;
        this.deleteTarifaDeliveryUseCase = deleteTarifaDeliveryUseCase;
        this.calcularCostoEnvioUseCase = calcularCostoEnvioUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TarifaDeliveryResponse>>> getAllTarifasDelivery(
            @RequestParam(required = false) Boolean isActive
    ) {
        Long tenantId = requireTenantId();
        List<TarifaDeliveryResponse> tarifas = getAllTarifasDeliveryUseCase.execute(tenantId, isActive);
        return ResponseEntity.ok(ApiResponse.success(tarifas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TarifaDeliveryResponse>> getTarifaDeliveryById(@PathVariable Long id) {
        Long tenantId = requireTenantId();
        TarifaDeliveryResponse tarifa = getTarifaDeliveryByIdUseCase.execute(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(tarifa));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TARIFA_DELIVERY_CONFIGURAR')")
    public ResponseEntity<ApiResponse<TarifaDeliveryResponse>> createTarifaDelivery(
            @Valid @RequestBody TarifaDeliveryRequest request
    ) {
        Long tenantId = requireTenantId();
        TarifaDeliveryResponse createdTarifa = createTarifaDeliveryUseCase.execute(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdTarifa));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TARIFA_DELIVERY_CONFIGURAR')")
    public ResponseEntity<ApiResponse<TarifaDeliveryResponse>> updateTarifaDelivery(
            @PathVariable Long id,
            @Valid @RequestBody TarifaDeliveryRequest request
    ) {
        Long tenantId = requireTenantId();
        TarifaDeliveryResponse updatedTarifa = updateTarifaDeliveryUseCase.execute(id, tenantId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedTarifa));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('TARIFA_DELIVERY_CONFIGURAR')")
    public ResponseEntity<ApiResponse<TarifaDeliveryResponse>> patchTarifaDelivery(
            @PathVariable Long id,
            @Valid @RequestBody TarifaDeliveryPatchRequest request
    ) {
        Long tenantId = requireTenantId();
        TarifaDeliveryResponse updatedTarifa = patchTarifaDeliveryUseCase.execute(id, tenantId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedTarifa));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TARIFA_DELIVERY_CONFIGURAR')")
    public ResponseEntity<ApiResponse<Void>> deleteTarifaDelivery(@PathVariable Long id) {
        Long tenantId = requireTenantId();
        deleteTarifaDeliveryUseCase.execute(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/calcular")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calcularCostoEnvio(
            @RequestParam @NotNull(message = "distanciaKm is required") @Positive(message = "distanciaKm must be greater than 0")
            BigDecimal distanciaKm
    ) {
        Long tenantId = requireTenantId();
        BigDecimal costoEnvio = calcularCostoEnvioUseCase.execute(tenantId, distanciaKm);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "distanciaKm", distanciaKm,
                "costoEnvio", costoEnvio
        )));
    }

    private Long requireTenantId() {
        Long tenantId = com.migestion.shared.security.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required"
            );
        }
        return tenantId;
    }
}
