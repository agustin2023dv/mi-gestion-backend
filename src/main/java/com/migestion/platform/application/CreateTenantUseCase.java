package com.migestion.platform.application;

import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.domain.PlanSuscripcionRepository;
import com.migestion.platform.dto.CreateTenantRequest;
import com.migestion.platform.dto.TenantResponse;
import com.migestion.platform.infrastructure.mapper.PlatformMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateTenantUseCase {

    private static final String PROPIETARIO_ROL = "propietario";

    private final TenantRepository tenantRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final PlanSuscripcionRepository planSuscripcionRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlatformMapper platformMapper;

    public TenantResponse execute(CreateTenantRequest request) {

        if (tenantRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new BusinessRuleViolationException(
                    "DUPLICATE_SLUG",
                    "Slug '" + request.getSlug() + "' is already in use");
        }

        if (tenantRepository.findByTenantIdentifier(request.getSlug()).isPresent()) {
            throw new BusinessRuleViolationException(
                    "DUPLICATE_TENANT_ID",
                    "Tenant identifier '" + request.getSlug() + "' is already in use");
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
        tenant = tenantRepository.saveAndFlush(tenant);

        // Create propietario UsuarioTenant with the provided password
        UsuarioTenant propietario = UsuarioTenant.builder()
                .tenantId(tenant.getId())
                .email(request.getPropietarioEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getPropietarioNombre())
                .apellido(request.getPropietarioApellido())
                .telefono(request.getPropietarioTelefono())
                .rol(PROPIETARIO_ROL)
                .build();
        propietario = usuarioTenantRepository.saveAndFlush(propietario);

        // Link propietario back to tenant
        tenant.setPropietarioId(propietario.getId());
        tenant = tenantRepository.saveAndFlush(tenant);

        TenantResponse.PropietarioResponse propietarioResponse = TenantResponse.PropietarioResponse.builder()
                .id(propietario.getId())
                .nombre(propietario.getNombre())
                .apellido(propietario.getApellido())
                .email(propietario.getEmail())
                .telefono(propietario.getTelefono())
                .build();

        return platformMapper.toTenantResponse(tenant, plan, propietarioResponse);
    }
}
