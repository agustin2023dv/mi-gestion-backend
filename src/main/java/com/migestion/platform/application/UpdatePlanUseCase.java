package com.migestion.platform.application;

import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.domain.PlanSuscripcionRepository;
import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.platform.dto.PlanSuscripcionResponse;
import com.migestion.platform.dto.UpdatePlanSuscripcionRequest;
import com.migestion.platform.infrastructure.mapper.PlatformMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePlanUseCase {

    private final SuperAdminRepository superAdminRepository;
    private final PlanSuscripcionRepository planSuscripcionRepository;
    private final PlatformMapper platformMapper;

    public PlanSuscripcionResponse execute(Long planId, UpdatePlanSuscripcionRequest request) {
        requireSuperAdmin();

        PlanSuscripcion plan = planSuscripcionRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("PlanSuscripcion", planId));

        if (request.getNombre() != null && !request.getNombre().equalsIgnoreCase(plan.getNombre())) {
            planSuscripcionRepository.findByNombreIgnoreCase(request.getNombre())
                    .ifPresent(existing -> {
                        throw new BusinessRuleViolationException(
                                "DUPLICATE_PLAN_NAME",
                                "A plan with name '" + request.getNombre() + "' already exists");
                    });
            plan.setNombre(request.getNombre());
        }
        if (request.getMaxProductos() != null) {
            plan.setMaxProductos(request.getMaxProductos());
        }
        if (request.getMaxPedidosMensuales() != null) {
            plan.setMaxPedidosMensuales(request.getMaxPedidosMensuales());
        }
        if (request.getMaxAlmacenamientoMb() != null) {
            plan.setMaxAlmacenamientoMb(request.getMaxAlmacenamientoMb());
        }
        if (request.getPrecioMensual() != null) {
            plan.setPrecioMensual(request.getPrecioMensual());
        }
        if (request.getFeatures() != null) {
            plan.setFeatures(request.getFeatures());
        }
        if (request.getOrderLevel() != null) {
            plan.setOrderLevel(request.getOrderLevel());
        }

        plan = planSuscripcionRepository.save(plan);
        return platformMapper.toPlanSuscripcionResponse(plan);
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
