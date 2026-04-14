package com.migestion.platform.application;

import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteTenantUseCase {

    private final SuperAdminRepository superAdminRepository;
    private final TenantRepository tenantRepository;

    public void execute(Long tenantId, String confirmacion) {
        requireSuperAdmin();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));

        if (!tenant.getNombreNegocio().equals(confirmacion)) {
            throw new BusinessRuleViolationException(
                    "CONFIRMATION_MISMATCH",
                    "Confirmation does not match the tenant's business name");
        }

        tenant.setDeletedAt(Instant.now());
        tenantRepository.save(tenant);
    }

    private void requireSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        AuthenticatedUserDetails userDetails = (AuthenticatedUserDetails) auth.getPrincipal();
        superAdminRepository.findByEmailIgnoreCase(userDetails.getEmail())
                .filter(SuperAdmin::isActive)
                .orElseThrow(() -> new AccessDeniedException("Access denied: SuperAdmin only"));
    }
}
