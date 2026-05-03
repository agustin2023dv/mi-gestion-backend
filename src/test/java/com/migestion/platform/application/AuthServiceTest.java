package com.migestion.platform.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SubdomainTenantResolver subdomainTenantResolver;
    @Mock
    private Authentication authentication;
    @Mock
    private Claims claims;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should login and generate both tokens for authenticated user")
    void should_login_and_generate_both_tokens_for_authenticated_user() {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("cliente@test.com")
                .password("Password123")
                .build();

        AuthenticatedUserDetails userDetails = AuthenticatedUserDetails.builder()
                .id(10L)
                .email("cliente@test.com")
                .password("encoded-password")
                .tenantId(22L)
                .role("CLIENTE")
                .permissions(List.of("PEDIDO_READ"))
                .userProfile(JwtResponse.UserProfile.builder()
                        .email("cliente@test.com")
                        .nombre("Test")
                        .apellido("User")
                        .build())
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(10L, 22L, "CLIENTE", List.of("PEDIDO_READ"), "cliente@test.com", "Test", "User"))
                .thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(10L, 22L, "CLIENTE", List.of("PEDIDO_READ"), "cliente@test.com", "Test", "User"))
                .thenReturn("refresh-token");

        // Act
        JwtResponse response = authService.login(request);

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(900L);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should hide authentication details when login credentials are invalid")
    void should_hide_authentication_details_when_login_credentials_are_invalid() {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("cliente@test.com")
                .password("wrong-password")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("Should register cliente with provided tenant without resolving another tenant")
    void should_register_cliente_with_provided_tenant_without_resolving_another_tenant() {
        // Arrange
        RegisterClienteRequest request = RegisterClienteRequest.builder()
                .nombre("Ana")
                .apellido("Lopez")
                .email("ana@test.com")
                .password("Password123")
                .tenantId(77L)
                .build();

        when(clienteRepository.findFirstByEmailIgnoreCase("ana@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
                        Cliente savedCliente = org.mockito.Mockito.mock(Cliente.class);
                        when(savedCliente.getId()).thenReturn(501L);
                        when(savedCliente.getEmail()).thenReturn("ana@test.com");
                        return savedCliente;
        });
        when(jwtTokenProvider.generateAccessToken(501L, 77L, "CLIENTE", List.of(), "ana@test.com", null, null))
                .thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(501L, 77L, "CLIENTE", List.of(), "ana@test.com", null, null))
                .thenReturn("refresh-token");

        ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);

        // Act
        JwtResponse response = authService.registerCliente(request);

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        verify(clienteRepository).save(clienteCaptor.capture());
        verify(subdomainTenantResolver, never()).resolve();
        assertThat(clienteCaptor.getValue().getTenantId()).isEqualTo(77L);
        assertThat(clienteCaptor.getValue().getEmail()).isEqualTo("ana@test.com");
        assertThat(clienteCaptor.getValue().getPasswordHash()).isEqualTo("encoded-password");
        assertThat(clienteCaptor.getValue().isEmailVerificado()).isFalse();
    }

    @Test
    @DisplayName("Should fail registration when email is already registered")
    void should_fail_registration_when_email_is_already_registered() {
        // Arrange
        RegisterClienteRequest request = RegisterClienteRequest.builder()
                .nombre("Ana")
                .apellido("Lopez")
                .email("ana@test.com")
                .password("Password123")
                .tenantId(77L)
                .build();

        when(clienteRepository.findFirstByEmailIgnoreCase("ana@test.com"))
                .thenReturn(Optional.of(org.mockito.Mockito.mock(Cliente.class)));

        // Act & Assert
        assertThatThrownBy(() -> authService.registerCliente(request))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Email address is already registered");

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

        @Test
        @DisplayName("Should resolve tenant via resolver when register request does not provide tenantId")
        void should_resolve_tenant_via_resolver_when_register_request_does_not_provide_tenant_id() {
                // Arrange
                RegisterClienteRequest request = RegisterClienteRequest.builder()
                                .nombre("Ana")
                                .apellido("Lopez")
                                .email("ana-resolved@test.com")
                                .password("Password123")
                                .tenantId(null)
                                .build();

                when(clienteRepository.findFirstByEmailIgnoreCase("ana-resolved@test.com")).thenReturn(Optional.empty());
                when(subdomainTenantResolver.resolve()).thenReturn(Optional.of(88L));
                when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");
                when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
                        Cliente savedCliente = org.mockito.Mockito.mock(Cliente.class);
                        when(savedCliente.getId()).thenReturn(601L);
                        when(savedCliente.getEmail()).thenReturn("ana-resolved@test.com");
                        return savedCliente;
                });
                when(jwtTokenProvider.generateAccessToken(601L, 88L, "CLIENTE", List.of(), "ana-resolved@test.com", null, null))
                                .thenReturn("access-token");
                when(jwtTokenProvider.generateRefreshToken(601L, 88L, "CLIENTE", List.of(), "ana-resolved@test.com", null, null))
                                .thenReturn("refresh-token");

                ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);

                // Act
                JwtResponse response = authService.registerCliente(request);

                // Assert
                assertThat(response.getAccessToken()).isEqualTo("access-token");
                assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
                verify(subdomainTenantResolver).resolve();
                verify(clienteRepository).save(clienteCaptor.capture());
                assertThat(clienteCaptor.getValue().getTenantId()).isEqualTo(88L);
        }

    @Test
    @DisplayName("Should refresh access token using tenant and role from refresh token claims")
    void should_refresh_access_token_using_tenant_and_role_from_refresh_token_claims() {
        // Arrange
        when(jwtTokenProvider.validateToken("refresh-token")).thenReturn(true);
        when(jwtTokenProvider.extractClaims("refresh-token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("11");
        when(claims.get("tenant_id", Long.class)).thenReturn(44L);
        when(claims.get("role", String.class)).thenReturn("CLIENTE");
        when(claims.get("email", String.class)).thenReturn("refreshed@test.com");
        when(claims.get("nombre", String.class)).thenReturn("Juan");
        when(claims.get("apellido", String.class)).thenReturn("Perez");
        when(jwtTokenProvider.generateAccessToken(11L, 44L, "CLIENTE", List.of(), "refreshed@test.com", "Juan", "Perez"))
                .thenReturn("new-access-token");

        // Act
        JwtResponse response = authService.refresh("refresh-token");

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getExpiresIn()).isEqualTo(900L);
    }

        @Test
        @DisplayName("Should invalidate access token on logout")
        void should_invalidate_access_token_on_logout() {
                // Arrange
                String accessToken = "access-token";

                // Act
                authService.logout(accessToken);

                // Assert
                verify(jwtTokenProvider).invalidateToken(accessToken);
        }
}