package com.migestion.notifications.application;

import com.migestion.notifications.domain.Notificacion;
import com.migestion.notifications.domain.NotificacionRepository;
import com.migestion.shared.exception.BusinessRuleViolationException;
import com.migestion.shared.exception.ResourceNotFoundException;
import com.migestion.shared.security.AuthenticatedUserDetails;
import com.migestion.shared.security.TenantContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarkNotificationAsReadUseCase {

    private final NotificacionRepository notificacionRepository;

    public MarkNotificationAsReadUseCase(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional
    public void execute(Long notificationId) {
        Long tenantId = requireTenantId();
        Long currentUserId = resolveCurrentUserId(requireAuthentication());

        Notificacion notification = notificacionRepository
                .findByIdAndTenantIdAndUsuarioId(notificationId, tenantId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion", notificationId));

        if (!notification.isLeido()) {
            notification.setLeido(true);
            notificacionRepository.save(notification);
        }
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
}
