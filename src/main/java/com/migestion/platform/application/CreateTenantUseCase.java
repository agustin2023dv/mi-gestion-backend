package com.migestion.platform.application;

import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.domain.PlanSuscripcionRepository;
import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.platform.dto.CreateTenantRequest;
import com.migestion.platform.dto.TenantResponse;
import com.migestion.platform.infrastructure.mapper.PlatformMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateTenantUseCase {

    private static final String PROPIETARIO_ROL = "propietario";

    private final SuperAdminRepository superAdminRepository;
    private final TenantRepository tenantRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final PlanSuscripcionRepository planSuscripcionRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlatformMapper platformMapper;

    public TenantResponse execute(CreateTenantRequest request) {
        requireSuperAdmin();

        if (tenantRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new BusinessRuleViolationException(
                    "DUPLICATE_SLUG",
                    "Slug '" + request.getSlug() + "' is already in use");
        }

        if (usuarioTenantRepository.existsByEmailIgnoreCase(request.getPropietarioEmail())) {
            throw new BusinessRuleViolationException(
                    "DUPLICATE_EMAIL",
                    "Email '" + request.getPropietarioEmail() + "' is already registered");
        }

        PlanSuscripcion plan = planSuscripcionRepository.findById(request.getPlanSuscripcionId())
                .orElseThrow(() -> new ResourceNotFoundException("PlanSuscripcion", request.getPlanSuscripcionId()));

        // Save Tenant first without propietarioId to break circular reference
        Tenant tenant = Tenant.builder()
                .tenantIdentifier(request.getSlug())
                .nombreNegocio(request.getNombreNegocio())
                .slug(request.getSlug())
                .planSuscripcionId(request.getPlanSuscripcionId())
                .build();
        tenant = tenantRepository.save(tenant);

        // Create propietario UsuarioTenant with a temporary random password
        String rawTempPassword = UUID.randomUUID().toString();
        UsuarioTenant propietario = UsuarioTenant.builder()
                .tenantId(tenant.getId())
                .email(request.getPropietarioEmail())
                .passwordHash(passwordEncoder.encode(rawTempPassword))
                .nombre(request.getPropietarioNombre())
                .apellido(request.getPropietarioApellido())
                .telefono(request.getPropietarioTelefono())
                .rol(PROPIETARIO_ROL)
                .build();
        propietario = usuarioTenantRepository.save(propietario);

        // Link propietario back to tenant
        tenant.setPropietarioId(propietario.getId());
        tenant = tenantRepository.save(tenant);

        TenantResponse.PropietarioResponse propietarioResponse = TenantResponse.PropietarioResponse.builder()
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
