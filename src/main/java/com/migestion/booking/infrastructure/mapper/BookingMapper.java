package com.migestion.booking.infrastructure.mapper;

import com.migestion.booking.domain.DisponibilidadServicio;
import com.migestion.booking.domain.Turno;
import com.migestion.booking.dto.DisponibilidadServicioResponse;
import com.migestion.booking.dto.TurnoResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BookingMapper {

    TurnoResponse toResponse(Turno turno);

    @Mapping(target = "isActive", source = "active")
    DisponibilidadServicioResponse toResponse(DisponibilidadServicio disponibilidad);
}
