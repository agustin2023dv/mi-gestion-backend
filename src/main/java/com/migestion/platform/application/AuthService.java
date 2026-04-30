package com.migestion.platform.application;

import com.migestion.platform.dto.JwtResponse;
import com.migestion.platform.dto.LoginRequest;
import com.migestion.platform.dto.RegisterClienteRequest;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.infrastructure.SubdomainTenantResolver;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.shared.security.JwtTokenProvider;
import com.migestion.tenant.domain.Cliente;
import com.migestion.tenant.domain.ClienteRepository;
import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service handling login, registration, token refresh, and logout.
 * Orchestrates authentication flows, JWT token generation, and user management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private static final String CLIENTE_ROLE = "CLIENTE";
  private static final long ACCESS_TOKEN_TTL_SECONDS = 900; // 15 minutes

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final ClienteRepository clienteRepository;
  private final PasswordEncoder passwordEncoder;
  private final SubdomainTenantResolver subdomainTenantResolver;

  /**
   * Authenticates a user with email and password.
   * Returns access and refresh tokens upon successful authentication.
   *
   * @param loginRequest containing email and password
   * @return JwtResponse with access and refresh tokens
   * @throws BusinessRuleViolationException if authentication fails
   */
  public JwtResponse login(LoginRequest loginRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(),
              loginRequest.getPassword()
          )
      );

      AuthenticatedUserDetails userDetails = (AuthenticatedUserDetails) authentication.getPrincipal();

      String accessToken = jwtTokenProvider.generateAccessToken(
          userDetails.getId(),
          userDetails.getTenantId(),
          userDetails.getRole(),
          userDetails.getPermissions()
      );

      String refreshToken = jwtTokenProvider.generateRefreshToken(
          userDetails.getId(),
          userDetails.getTenantId(),
          userDetails.getRole(),
          userDetails.getPermissions()
      );

      return JwtResponse.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .tokenType("Bearer")
          .expiresIn(ACCESS_TOKEN_TTL_SECONDS)
          .user(userDetails.getUserProfile())
          .build();

    } catch (Exception ex) {
      throw new BusinessRuleViolationException("INVALID_CREDENTIALS", "Invalid email or password");
    }
  }

  /**
   * Registers a new Cliente user.
   * Hashes the password, saves to database, and returns JWT tokens.
   * Tenant ID is resolved from the request or environment context.
   *
   * @param registerRequest containing email, password, and optional tenantId
   * @return JwtResponse with access and refresh tokens
   * @throws BusinessRuleViolationException if email already exists
   */
  public JwtResponse registerCliente(RegisterClienteRequest registerRequest) {
    // Check if email already exists
    if (clienteRepository.findFirstByEmailIgnoreCase(registerRequest.getEmail()).isPresent()) {
      throw new BusinessRuleViolationException(
          "EMAIL_ALREADY_REGISTERED",
          "Email address is already registered"
      );
    }

    Long tenantId = resolveTenantId(registerRequest.getTenantId());

    // Create and save new cliente
    String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
    Cliente cliente = Cliente.builder()
        .tenantId(tenantId)
        .email(registerRequest.getEmail())
        .passwordHash(hashedPassword)
        .emailVerificado(false)
        .build();

    Cliente savedCliente = clienteRepository.save(cliente);

    // Generate tokens
    String accessToken = jwtTokenProvider.generateAccessToken(
        savedCliente.getId(),
        tenantId,
        CLIENTE_ROLE,
        List.of()
    );

    String refreshToken = jwtTokenProvider.generateRefreshToken(
        savedCliente.getId(),
        tenantId,
        CLIENTE_ROLE,
        List.of()
    );

    return JwtResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(ACCESS_TOKEN_TTL_SECONDS)
        .build();
  }

  /**
   * Validates a refresh token and issues a new access token.
   * The refresh token must be valid and not expired or blacklisted.
   *
   * @param refreshToken the refresh token provided by the client
   * @return JwtResponse with new access token
   * @throws BusinessRuleViolationException if refresh token is invalid
   */
  @Transactional(readOnly = true)
  public JwtResponse refresh(String refreshToken) {
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new BusinessRuleViolationException(
          "INVALID_REFRESH_TOKEN",
          "Refresh token is invalid or expired"
      );
    }

    Claims claims = jwtTokenProvider.extractClaims(refreshToken);
    Long userId = Long.parseLong(claims.getSubject());
    Long tenantId = claims.get("tenant_id", Long.class);
    String role = claims.get("role", String.class);

    String newAccessToken = jwtTokenProvider.generateAccessToken(
        userId,
        tenantId,
        role,
        List.of()
    );

    return JwtResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(ACCESS_TOKEN_TTL_SECONDS)
        .build();
  }

  /**
   * Logs out a user by invalidating their access token.
   * The token is added to Redis blacklist to prevent further use.
   *
   * @param accessToken the access token to invalidate
   */
  public void logout(String accessToken) {
    jwtTokenProvider.invalidateToken(accessToken);
  }

  /**
   * Resolves the tenant ID from the register request or current context.
   * If not provided in request, attempts to resolve from context or throws exception.
   *
   * @param providedTenantId tenant ID from request (optional)
   * @return resolved tenant ID
   * @throws BusinessRuleViolationException if tenant cannot be resolved
   */
  private Long resolveTenantId(Long providedTenantId) {
    if (providedTenantId != null) {
      return providedTenantId;
    }

    // Resolve from: subdomain → X-Tenant-Slug header → tenantSlug query param
    return subdomainTenantResolver.resolve()
        .orElseThrow(() -> new BusinessRuleViolationException(
            "TENANT_RESOLUTION_FAILED",
            "Tenant could not be resolved from subdomain, X-Tenant-Slug header, or tenantSlug parameter"
        ));
  }
}
