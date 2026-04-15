package com.migestion.logistics.infrastructure;

import com.migestion.logistics.application.GenerateFirmaDigitalUseCase;
import com.migestion.logistics.application.GetFirmaByEntregaUseCase;
import com.migestion.logistics.application.GetFirmaSessionUseCase;
import com.migestion.logistics.application.VerifyFirmaUseCase;
import com.migestion.logistics.dto.FirmaEntregaResponse;
import com.migestion.logistics.dto.FirmaResponse;
import com.migestion.logistics.dto.FirmaSessionResponse;
import com.migestion.logistics.dto.GenerateFirmaRequest;
import com.migestion.logistics.dto.VerifyFirmaRequest;
import com.migestion.logistics.dto.VerifyFirmaResponse;
import com.migestion.shared.dto.ApiResponse;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.shared.security.TenantContext;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/firmas")
public class FirmaDigitalController {

    private final GenerateFirmaDigitalUseCase generateFirmaDigitalUseCase;
    private final GetFirmaSessionUseCase getFirmaSessionUseCase;
    private final VerifyFirmaUseCase verifyFirmaUseCase;
    private final GetFirmaByEntregaUseCase getFirmaByEntregaUseCase;

    public FirmaDigitalController(
            GenerateFirmaDigitalUseCase generateFirmaDigitalUseCase,
            GetFirmaSessionUseCase getFirmaSessionUseCase,
            VerifyFirmaUseCase verifyFirmaUseCase,
            GetFirmaByEntregaUseCase getFirmaByEntregaUseCase
    ) {
        this.generateFirmaDigitalUseCase = generateFirmaDigitalUseCase;
        this.getFirmaSessionUseCase = getFirmaSessionUseCase;
        this.verifyFirmaUseCase = verifyFirmaUseCase;
        this.getFirmaByEntregaUseCase = getFirmaByEntregaUseCase;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('ENTREGA_UPDATE_STATUS')")
    public ResponseEntity<ApiResponse<FirmaResponse>> generate(
            @Valid @RequestBody GenerateFirmaRequest request,
            @AuthenticationPrincipal AuthenticatedUserDetails user
    ) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required"
            );
        }

        FirmaResponse response = generateFirmaDigitalUseCase.execute(request.entregaId(), user.getId(), tenantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<FirmaSessionResponse>> getByToken(@PathVariable String token) {
        FirmaSessionResponse response = getFirmaSessionUseCase.execute(token);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{token}/verify")
    public ResponseEntity<ApiResponse<VerifyFirmaResponse>> verify(
            @PathVariable String token,
            @Valid @RequestBody VerifyFirmaRequest request
    ) {
        VerifyFirmaResponse response = verifyFirmaUseCase.execute(
                token,
                request.firmaDatos(),
                request.dispositivoInfo()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/entrega/{entregaId}")
    @PreAuthorize("hasAuthority('ENTREGA_VIEW')")
    public ResponseEntity<ApiResponse<FirmaEntregaResponse>> getByEntrega(@PathVariable Long entregaId) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required"
            );
        }

        FirmaEntregaResponse response = getFirmaByEntregaUseCase.execute(entregaId, tenantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}