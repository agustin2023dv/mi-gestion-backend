package com.migestion.platform.application;

import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.domain.PlanSuscripcionRepository;
import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.platform.dto.PlanSuscripcionResponse;
import com.migestion.platform.infrastructure.mapper.PlatformMapper;
import com.migestion.shared.security.AuthenticatedUserDetails;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllPlansUseCase {

    private final SuperAdminRepository superAdminRepository;
    private final PlanSuscripcionRepository planSuscripcionRepository;
    private final PlatformMapper platformMapper;

    public List<PlanSuscripcionResponse> execute() {
        requireSuperAdmin();

        return planSuscripcionRepository.findAll().stream()
                .sorted(Comparator.comparingInt(PlanSuscripcion::getOrderLevel))
                .map(platformMapper::toPlanSuscripcionResponse)
                .toList();
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
