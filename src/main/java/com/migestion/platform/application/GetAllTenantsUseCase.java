package com.migestion.platform.application;

import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.domain.PlanSuscripcionRepository;
import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.platform.dto.PageResponse;
import com.migestion.platform.dto.TenantResponse;
import com.migestion.platform.infrastructure.mapper.PlatformMapper;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllTenantsUseCase {

    private final SuperAdminRepository superAdminRepository;
    private final TenantRepository tenantRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final PlanSuscripcionRepository planSuscripcionRepository;
    private final PlatformMapper platformMapper;

    public PageResponse<TenantResponse> execute(
            Boolean isActive,
            Boolean isSuspended,
            Long planSuscripcionId,
            String search,
            Pageable pageable) {
        requireSuperAdmin();

        Page<Tenant> tenantPage = tenantRepository.findAllWithFilters(
                isActive, isSuspended, planSuscripcionId, search, pageable);

        List<Long> planIds = tenantPage.getContent().stream()
                .filter(t -> t.getPlanSuscripcionId() != null)
                .map(Tenant::getPlanSuscripcionId)
                .distinct()
                .toList();

        List<Long> propietarioIds = tenantPage.getContent().stream()
                .filter(t -> t.getPropietarioId() != null)
                .map(Tenant::getPropietarioId)
                .distinct()
                .toList();

        Map<Long, PlanSuscripcion> plansById = planSuscripcionRepository.findAllById(planIds)
                .stream().collect(Collectors.toMap(PlanSuscripcion::getId, p -> p));

        Map<Long, UsuarioTenant> propietariosById = usuarioTenantRepository.findAllById(propietarioIds)
                .stream().collect(Collectors.toMap(UsuarioTenant::getId, u -> u));

        List<TenantResponse> content = tenantPage.getContent().stream()
                .map(tenant -> buildTenantResponse(tenant, plansById, propietariosById))
                .toList();

        return PageResponse.<TenantResponse>builder()
                .content(content)
                .pageNumber(tenantPage.getNumber())
                .pageSize(tenantPage.getSize())
                .totalElements(tenantPage.getTotalElements())
                .totalPages(tenantPage.getTotalPages())
                .hasNext(tenantPage.hasNext())
                .hasPrevious(tenantPage.hasPrevious())
                .build();
    }

    private TenantResponse buildTenantResponse(
            Tenant tenant,
            Map<Long, PlanSuscripcion> plansById,
            Map<Long, UsuarioTenant> propietariosById) {
        PlanSuscripcion plan = tenant.getPlanSuscripcionId() != null
                ? plansById.get(tenant.getPlanSuscripcionId()) : null;
        UsuarioTenant propietario = tenant.getPropietarioId() != null
                ? propietariosById.get(tenant.getPropietarioId()) : null;
        TenantResponse.PropietarioResponse propietarioResponse = toPropietarioResponse(propietario);
        return platformMapper.toTenantResponse(tenant, plan, propietarioResponse);
    }

    private TenantResponse.PropietarioResponse toPropietarioResponse(UsuarioTenant propietario) {
        if (propietario == null) {
            return null;
        }
        return TenantResponse.PropietarioResponse.builder()
                .id(propietario.getId())
                .nombre(propietario.getNombre())
                .apellido(propietario.getApellido())
                .email(propietario.getEmail())
                .telefono(propietario.getTelefono())
                .build();
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
