package com.migestion.shared.filter;

import com.migestion.shared.security.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_CLAIM = "tenant_id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            Long tenantId = resolveTenantIdFromSecurityContext();
            if (tenantId != null) {
                TenantContext.setTenantId(tenantId);
            } else if (!isPublicEndpoint(request)) {
                throw new AccessDeniedException("Tenant context is required for this endpoint");
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private Long resolveTenantIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Long fromPrincipal = extractTenantId(authentication.getPrincipal());
        if (fromPrincipal != null) {
            return fromPrincipal;
        }

        Long fromDetails = extractTenantId(authentication.getDetails());
        if (fromDetails != null) {
            return fromDetails;
        }

        return extractTenantId(authentication);
    }

    private Long extractTenantId(Object source) {
        if (source == null) {
            return null;
        }

        if (source instanceof Map<?, ?> map) {
            return toLong(map.get(TENANT_CLAIM));
        }

        Long fromClaimMethod = invokeGetClaim(source);
        if (fromClaimMethod != null) {
            return fromClaimMethod;
        }

        Long fromClaimsMap = invokeGetClaims(source);
        if (fromClaimsMap != null) {
            return fromClaimsMap;
        }

        return invokeGetTenantId(source);
    }

    private Long invokeGetClaim(Object source) {
        try {
            Method getClaim = source.getClass().getMethod("getClaim", String.class);
            return toLong(getClaim.invoke(source, TENANT_CLAIM));
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Long invokeGetClaims(Object source) {
        try {
            Method getClaims = source.getClass().getMethod("getClaims");
            Object claims = getClaims.invoke(source);
            if (claims instanceof Map<?, ?> map) {
                return toLong(((Map<String, Object>) map).get(TENANT_CLAIM));
            }
            return null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private Long invokeGetTenantId(Object source) {
        try {
            Method getTenantId = source.getClass().getMethod("getTenantId");
            return toLong(getTenantId.invoke(source));
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        if (value instanceof String raw && !raw.isBlank()) {
            try {
                return Long.parseLong(raw);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if (path == null || path.isBlank()) {
            return false;
        }

        if (path.startsWith("/api/v1/auth")) {
            return true;
        }

        if (path.startsWith("/actuator/health")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")) {
            return true;
        }

        return "GET".equalsIgnoreCase(method)
                && (path.startsWith("/api/v1/categorias") || path.startsWith("/api/v1/subcategorias"));
    }
}
