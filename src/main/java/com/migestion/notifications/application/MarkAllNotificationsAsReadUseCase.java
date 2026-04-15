package com.migestion.notifications.application;

import com.migestion.notifications.domain.NotificacionRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.shared.security.TenantContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarkAllNotificationsAsReadUseCase {

    private final NotificacionRepository notificacionRepository;

    public MarkAllNotificationsAsReadUseCase(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public int execute() {
        Long tenantId = requireTenantId();
        Authentication authentication = requireAuthentication();
        Long currentUserId = resolveCurrentUserId(authentication);
        String currentUserRole = resolveCurrentUserRole(authentication);

        return notificacionRepository.markAllAsRead(tenantId, currentUserId, currentUserRole);
    }

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new BusinessRuleViolationException(
                    "TENANT_CONTEXT_REQUIRED",
                    "Tenant context is required");
        }
        return tenantId;
    }

    private Authentication requireAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        return authentication;
    }

    private Long resolveCurrentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUserDetails userDetails) {
            return userDetails.getId();
        }

        String subject = authentication.getName();
        if (subject != null && !subject.isBlank()) {
            try {
                return Long.parseLong(subject);
            } catch (NumberFormatException ignored) {
                throw new AccessDeniedException("Invalid authenticated principal");
            }
        }

        throw new AccessDeniedException("Invalid authenticated principal");
    }

    private String resolveCurrentUserRole(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUserDetails userDetails) {
            return userDetails.getRole();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Authenticated role is required"));
    }
}