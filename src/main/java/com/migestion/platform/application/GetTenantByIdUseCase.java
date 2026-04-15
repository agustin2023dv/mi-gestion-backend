package com.migestion.platform.application;

import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.domain.PlanSuscripcionRepository;
import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.platform.dto.TenantResponse;
import com.migestion.platform.infrastructure.mapper.PlatformMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTenantByIdUseCase {

    private final SuperAdminRepository superAdminRepository;
    private final TenantRepository tenantRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final PlanSuscripcionRepository planSuscripcionRepository;
    private final PlatformMapper platformMapper;

    public TenantResponse execute(Long tenantId) {
        requireSuperAdmin();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));

        PlanSuscripcion plan = tenant.getPlanSuscripcionId() != null
                ? planSuscripcionRepository.findById(tenant.getPlanSuscripcionId()).orElse(null)
                : null;

        UsuarioTenant propietario = tenant.getPropietarioId() != null
                ? usuarioTenantRepository.findById(tenant.getPropietarioId()).orElse(null)
                : null;

        TenantResponse.PropietarioResponse propietarioResponse = propietario == null ? null
                : TenantResponse.PropietarioResponse.builder()
                        .id(propietario.getId())
                        .nombre(propietario.getNombre())
                        .apellido(propietario.getApellido())
                        .email(propietario.getEmail())
                        .telefono(propietario.getTelefono())
                        .build();

        return platformMapper.toTenantResponse(tenant, plan, propietarioResponse);
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
