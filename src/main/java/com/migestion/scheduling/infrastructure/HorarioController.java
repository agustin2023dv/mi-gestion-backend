package com.migestion.scheduling.infrastructure;

import com.migestion.scheduling.application.CreateBloqueoHorarioUseCase;
import com.migestion.scheduling.application.CreateExcepcionHorarioUseCase;
import com.migestion.scheduling.application.DeleteBloqueoHorarioUseCase;
import com.migestion.scheduling.application.GetAllHorariosAtencionUseCase;
import com.migestion.scheduling.application.UpsertHorarioAtencionUseCase;
import com.migestion.scheduling.dto.BloqueoHorarioRequest;
import com.migestion.scheduling.dto.BloqueoHorarioResponse;
import com.migestion.scheduling.dto.ExcepcionHorarioRequest;
import com.migestion.scheduling.dto.ExcepcionHorarioResponse;
import com.migestion.scheduling.dto.HorarioAtencionRequest;
import com.migestion.scheduling.dto.HorarioAtencionResponse;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.TenantContext;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/horarios")
public class HorarioController {

    private final GetAllHorariosAtencionUseCase getAllHorariosAtencionUseCase;
    private final UpsertHorarioAtencionUseCase upsertHorarioAtencionUseCase;
    private final CreateExcepcionHorarioUseCase createExcepcionHorarioUseCase;
    private final CreateBloqueoHorarioUseCase createBloqueoHorarioUseCase;
    private final DeleteBloqueoHorarioUseCase deleteBloqueoHorarioUseCase;

    public HorarioController(
            GetAllHorariosAtencionUseCase getAllHorariosAtencionUseCase,
            UpsertHorarioAtencionUseCase upsertHorarioAtencionUseCase,
            CreateExcepcionHorarioUseCase createExcepcionHorarioUseCase,
            CreateBloqueoHorarioUseCase createBloqueoHorarioUseCase,
            DeleteBloqueoHorarioUseCase deleteBloqueoHorarioUseCase) {
        this.getAllHorariosAtencionUseCase = getAllHorariosAtencionUseCase;
        this.upsertHorarioAtencionUseCase = upsertHorarioAtencionUseCase;
        this.createExcepcionHorarioUseCase = createExcepcionHorarioUseCase;
        this.createBloqueoHorarioUseCase = createBloqueoHorarioUseCase;
        this.deleteBloqueoHorarioUseCase = deleteBloqueoHorarioUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HorarioAtencionResponse>>> getAllHorarios() {
        Long tenantId = requireTenantId();
        List<HorarioAtencionResponse> horarios = getAllHorariosAtencionUseCase.execute(tenantId);
        return ResponseEntity.ok(ApiResponse.success(horarios));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<HorarioAtencionResponse>> upsertHorario(
            @Valid @RequestBody HorarioAtencionRequest request) {
        Long tenantId = requireTenantId();
        HorarioAtencionResponse response = upsertHorarioAtencionUseCase.execute(tenantId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/excepciones")
    public ResponseEntity<ApiResponse<ExcepcionHorarioResponse>> createExcepcion(
            @Valid @RequestBody ExcepcionHorarioRequest request) {
        Long tenantId = requireTenantId();
        ExcepcionHorarioResponse response = createExcepcionHorarioUseCase.execute(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PostMapping("/bloqueos")
    public ResponseEntity<ApiResponse<BloqueoHorarioResponse>> createBloqueo(
            @Valid @RequestBody BloqueoHorarioRequest request) {
        Long tenantId = requireTenantId();
        BloqueoHorarioResponse response = createBloqueoHorarioUseCase.execute(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @DeleteMapping("/bloqueos/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBloqueo(@PathVariable Long id) {
        Long tenantId = requireTenantId();
        deleteBloqueoHorarioUseCase.execute(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success());
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
