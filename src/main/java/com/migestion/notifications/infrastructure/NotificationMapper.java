package com.migestion.notifications.infrastructure;

import com.migestion.notifications.domain.ConfiguracionNotificacion;
import com.migestion.notifications.domain.Notificacion;
import com.migestion.notifications.dto.ConfiguracionNotificacionResponse;
import com.migestion.notifications.dto.NotificacionResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface NotificationMapper {

    NotificacionResponse toResponse(Notificacion notificacion);

    ConfiguracionNotificacionResponse toResponse(ConfiguracionNotificacion configuracionNotificacion);
}
