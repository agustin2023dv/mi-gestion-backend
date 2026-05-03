package com.migestion.shared.security;

import com.migestion.platform.dto.JwtResponse.UserProfile;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TENANT_ID_CLAIM = "tenant_id";
    private static final String ROLE_CLAIM = "role";
    private static final String PERMISSIONS_CLAIM = "permissions";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            if (jwtTokenProvider.validateToken(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                Claims claims = jwtTokenProvider.extractClaims(token);
                AuthenticatedUserDetails userDetails = buildUserDetails(claims);
                
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(extractDetails(claims));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private AuthenticatedUserDetails buildUserDetails(Claims claims) {
        Long userId = Long.parseLong(claims.getSubject());
        Long tenantId = claims.get(TENANT_ID_CLAIM, Long.class);
        String role = claims.get(ROLE_CLAIM, String.class);
        String email = claims.get("email", String.class);
        String nombre = claims.get("nombre", String.class);
        String apellido = claims.get("apellido", String.class);

        List<String> permissions = new ArrayList<>();
        Object permissionsClaim = claims.get(PERMISSIONS_CLAIM);
        if (permissionsClaim instanceof Collection<?> permissionValues) {
            permissionValues.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(permissions::add);
        }

        UserProfile profile = UserProfile.builder()
                .id(userId)
                .email(email)
                .nombre(nombre)
                .apellido(apellido)
                .role(role)
                .tenantId(tenantId)
                .build();

        return AuthenticatedUserDetails.builder()
                .id(userId)
                .email(email)
                .tenantId(tenantId)
                .role(role)
                .permissions(permissions)
                .userProfile(profile)
                .build();
    }

    private Map<String, Object> extractDetails(Claims claims) {
        Map<String, Object> details = new HashMap<>();
        details.put(TENANT_ID_CLAIM, claims.get(TENANT_ID_CLAIM));
        details.put(Claims.SUBJECT, claims.getSubject());
        details.put(Claims.ID, claims.getId());
        return details;
    }
}