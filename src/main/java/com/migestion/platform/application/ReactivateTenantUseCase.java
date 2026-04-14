package com.migestion.platform.application;

import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.platform.dto.TenantResponse;
import com.migestion.platform.infrastructure.mapper.PlatformMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReactivateTenantUseCase {

    private final SuperAdminRepository superAdminRepository;
    private final TenantRepository tenantRepository;
    private final PlatformMapper platformMapper;

    public TenantResponse execute(Long tenantId) {
        requireSuperAdmin();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));

        tenant.setSuspended(false);
        tenant = tenantRepository.save(tenant);

        return platformMapper.toTenantResponse(tenant);
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
