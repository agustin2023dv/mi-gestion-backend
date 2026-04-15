package com.migestion.notifications.infrastructure;

import com.migestion.notifications.domain.ConfiguracionNotificacion;
import com.migestion.notifications.domain.Notificacion;
import com.migestion.notifications.dto.NotificationPreferencesResponse;
import com.migestion.notifications.dto.NotificationResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface NotificationMapper {

    NotificationResponse toResponse(Notificacion notificacion);

    NotificationPreferencesResponse toResponse(ConfiguracionNotificacion configuracionNotificacion);
}
