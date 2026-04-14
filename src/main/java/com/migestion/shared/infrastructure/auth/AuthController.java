package com.migestion.shared.infrastructure.auth;

import com.migestion.platform.application.AuthService;
import com.migestion.platform.dto.JwtResponse;
import com.migestion.platform.dto.LoginRequest;
import com.migestion.platform.dto.RefreshTokenRequest;
import com.migestion.platform.dto.RegisterClienteRequest;
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

  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
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

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<JwtResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    JwtResponse jwtResponse = authService.refresh(request.getRefreshToken());
    return ResponseEntity.ok(ApiResponse.success(jwtResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader
  ) {
    String accessToken = extractBearerToken(authorizationHeader);
    authService.logout(accessToken);
    return ResponseEntity.ok(ApiResponse.success());
  }

  private String extractBearerToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
    }
    return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
  }
}