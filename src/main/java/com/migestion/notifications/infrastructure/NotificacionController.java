package com.migestion.notifications.infrastructure;

import com.migestion.notifications.application.GetNotificationPreferencesUseCase;
import com.migestion.notifications.application.GetUserNotificationsUseCase;
import com.migestion.notifications.application.MarkAllNotificationsAsReadUseCase;
import com.migestion.notifications.application.MarkNotificationAsReadUseCase;
import com.migestion.notifications.application.SendNotificationUseCase;
import com.migestion.notifications.application.UpdateNotificationPreferencesUseCase;
import com.migestion.notifications.dto.ConfiguracionNotificacionResponse;
import com.migestion.notifications.dto.NotificacionResponse;
import com.migestion.notifications.dto.NotificationRequest;
import com.migestion.notifications.dto.PageResponse;
import com.migestion.notifications.dto.UpdateNotificationPreferencesRequest;
import com.migestion.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private final GetUserNotificationsUseCase getUserNotificationsUseCase;
    private final MarkNotificationAsReadUseCase markNotificationAsReadUseCase;
    private final MarkAllNotificationsAsReadUseCase markAllNotificationsAsReadUseCase;
    private final GetNotificationPreferencesUseCase getNotificationPreferencesUseCase;
    private final UpdateNotificationPreferencesUseCase updateNotificationPreferencesUseCase;
    private final SendNotificationUseCase sendNotificationUseCase;

    public NotificacionController(
            GetUserNotificationsUseCase getUserNotificationsUseCase,
            MarkNotificationAsReadUseCase markNotificationAsReadUseCase,
            MarkAllNotificationsAsReadUseCase markAllNotificationsAsReadUseCase,
            GetNotificationPreferencesUseCase getNotificationPreferencesUseCase,
            UpdateNotificationPreferencesUseCase updateNotificationPreferencesUseCase,
            SendNotificationUseCase sendNotificationUseCase) {
        this.getUserNotificationsUseCase = getUserNotificationsUseCase;
        this.markNotificationAsReadUseCase = markNotificationAsReadUseCase;
        this.markAllNotificationsAsReadUseCase = markAllNotificationsAsReadUseCase;
        this.getNotificationPreferencesUseCase = getNotificationPreferencesUseCase;
        this.updateNotificationPreferencesUseCase = updateNotificationPreferencesUseCase;
        this.sendNotificationUseCase = sendNotificationUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificacionResponse>>> getNotifications(Pageable pageable) {
        PageResponse<NotificacionResponse> response = getUserNotificationsUseCase.execute(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        markNotificationAsReadUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/marcar-todas-leidas")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        markAllNotificationsAsReadUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/configuracion")
    public ResponseEntity<ApiResponse<ConfiguracionNotificacionResponse>> getConfiguracion() {
        ConfiguracionNotificacionResponse response = getNotificationPreferencesUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/configuracion")
    public ResponseEntity<ApiResponse<ConfiguracionNotificacionResponse>> updateConfiguracion(
            @Valid @RequestBody UpdateNotificationPreferencesRequest request) {
        ConfiguracionNotificacionResponse response = updateNotificationPreferencesUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/enviar")
    public ResponseEntity<ApiResponse<NotificacionResponse>> enviar(
            @Valid @RequestBody NotificationRequest request,
            Authentication authentication) {
        requireTenantAdmin(authentication);
        NotificacionResponse response = sendNotificationUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    private void requireTenantAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Tenant admin authentication is required");
        }

        Collection<String> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        boolean isTenantAdmin = authorities.contains("ROLE_TENANT_ADMIN")
                || authorities.contains("ROLE_TENANTADMIN")
                || authorities.contains("ROLE_ADMIN");

        if (!isTenantAdmin) {
            throw new AccessDeniedException("Only TenantAdmin can send notifications");
        }
    }
}