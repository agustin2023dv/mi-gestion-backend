package com.migestion.shared.infrastructure.auth;

import com.migestion.platform.application.AuthService;
import com.migestion.platform.application.CreateTenantUseCase;
import com.migestion.platform.dto.CreateTenantRequest;
import com.migestion.platform.dto.JwtResponse;
import com.migestion.platform.dto.LoginRequest;
import com.migestion.platform.dto.RefreshTokenRequest;
import com.migestion.platform.dto.RegisterClienteRequest;
import com.migestion.platform.dto.TenantResponse;
import com.migestion.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);
  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthService authService;
  private final CreateTenantUseCase createTenantUseCase;

  public AuthController(AuthService authService, CreateTenantUseCase createTenantUseCase) {
    this.authService = authService;
    this.createTenantUseCase = createTenantUseCase;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
    JwtResponse jwtResponse = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success(jwtResponse));
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<JwtResponse>> register(@Valid @RequestBody RegisterClienteRequest request) {
    JwtResponse jwtResponse = authService.registerCliente(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(jwtResponse));
  }

  @PostMapping("/register-tenant")
  public ResponseEntity<ApiResponse<TenantResponse>> registerTenant(@Valid @RequestBody CreateTenantRequest request) {
    TenantResponse tenantResponse = createTenantUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tenantResponse));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<JwtResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    JwtResponse jwtResponse = authService.refresh(request.getRefreshToken());
    return ResponseEntity.ok(ApiResponse.success(jwtResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
  ) {
    log.debug("Processing logout request");
    try {
      String accessToken = extractBearerToken(authorizationHeader);
      authService.logout(accessToken);
      log.debug("Logout completed successfully");
      return ResponseEntity.ok(ApiResponse.success());
    } catch (ResponseStatusException e) {
      log.warn("Logout rejected (status {}): {}", e.getStatusCode(), e.getReason());
      throw e;
    } catch (Exception e) {
      log.error("Unexpected internal error during logout: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error during logout process");
    }
  }

  private String extractBearerToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
    }
    return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
  }
}