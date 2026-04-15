package com.migestion.shared.infrastructure;

import com.migestion.tenant.domain.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves the current tenant from the HTTP request using a three-step strategy:
 * <ol>
 *   <li>Subdomain extracted from the {@code Host} header
 *       (e.g. {@code negocio.mi-gestion.com} → {@code "negocio"}).</li>
 *   <li>{@code X-Tenant-Slug} request header – development / staging fallback.</li>
 *   <li>{@code tenantSlug} query parameter – local-development fallback.</li>
 * </ol>
 * Once a slug is found it is looked up in the database via {@link TenantRepository}.
 */
@Component
@RequiredArgsConstructor
public class SubdomainTenantResolver {

    private static final String TENANT_SLUG_HEADER = "X-Tenant-Slug";
    private static final String TENANT_SLUG_PARAM  = "tenantSlug";

    private final TenantRepository tenantRepository;
    private final HttpServletRequest httpServletRequest;

    /**
     * Attempts to resolve the tenant ID from the current HTTP request.
     *
     * @return an {@link Optional} containing the tenant's database ID,
     *         or empty if no tenant could be resolved.
     */
    public Optional<Long> resolve() {
        Optional<String> slug = extractFromSubdomain()
                .or(this::extractFromHeader)
                .or(this::extractFromQueryParam);

        return slug.flatMap(tenantRepository::findBySlug)
                   .map(tenant -> tenant.getId());
    }

    // -------------------------------------------------------------------------
    // private helpers
    // -------------------------------------------------------------------------

    private Optional<String> extractFromSubdomain() {
        String host = httpServletRequest.getHeader("Host");
        if (host == null || host.isBlank()) {
            return Optional.empty();
        }

        // Strip optional port (e.g. "negocio.mi-gestion.com:8080" → "negocio.mi-gestion.com")
        String hostname = host.split(":")[0];

        if (isLocalAddress(hostname)) {
            return Optional.empty();
        }

        // A hostname with 3+ dot-separated labels carries a subdomain in the first position
        String[] parts = hostname.split("\\.");
        if (parts.length >= 3) {
            String subdomain = parts[0];
            return subdomain.isBlank() ? Optional.empty() : Optional.of(subdomain);
        }

        return Optional.empty();
    }

    private Optional<String> extractFromHeader() {
        return Optional.ofNullable(httpServletRequest.getHeader(TENANT_SLUG_HEADER))
                       .filter(s -> !s.isBlank());
    }

    private Optional<String> extractFromQueryParam() {
        return Optional.ofNullable(httpServletRequest.getParameter(TENANT_SLUG_PARAM))
                       .filter(s -> !s.isBlank());
    }

    /**
     * Returns {@code true} for hostnames that point to the local machine and
     * therefore cannot carry a meaningful subdomain.
     */
    private boolean isLocalAddress(String hostname) {
        return "localhost".equalsIgnoreCase(hostname)
                || hostname.startsWith("127.")
                || hostname.startsWith("192.168.")
                || "::1".equals(hostname);
    }
}
