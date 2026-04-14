package com.migestion.platform.application;

import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.domain.PlanSuscripcionRepository;
import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.platform.dto.CreatePlanRequest;
import com.migestion.platform.dto.PlanSuscripcionResponse;
import com.migestion.platform.infrastructure.mapper.PlatformMapper;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatePlanUseCase {

    private final SuperAdminRepository superAdminRepository;
    private final PlanSuscripcionRepository planSuscripcionRepository;
    private final PlatformMapper platformMapper;

    public PlanSuscripcionResponse execute(CreatePlanRequest request) {
        requireSuperAdmin();

        if (planSuscripcionRepository.findByNombreIgnoreCase(request.getNombre()).isPresent()) {
            throw new BusinessRuleViolationException(
                    "DUPLICATE_PLAN_NAME",
                    "A plan with name '" + request.getNombre() + "' already exists");
        }

        List<String> features = request.getFeatures() != null ? request.getFeatures() : List.of();

        PlanSuscripcion plan = PlanSuscripcion.builder()
                .nombre(request.getNombre())
                .maxProductos(request.getMaxProductos())
                .maxPedidosMensuales(request.getMaxPedidosMensuales())
                .maxAlmacenamientoMb(request.getMaxAlmacenamientoMb())
                .precioMensual(request.getPrecioMensual())
                .features(features)
                .orderLevel(request.getOrderLevel())
                .build();

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
