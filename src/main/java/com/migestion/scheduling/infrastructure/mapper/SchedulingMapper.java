package com.migestion.scheduling.infrastructure.mapper;

import com.migestion.scheduling.domain.BloqueoHorario;
import com.migestion.scheduling.domain.ExcepcionHorario;
import com.migestion.scheduling.domain.HorarioAtencion;
import com.migestion.scheduling.dto.BloqueoHorarioResponse;
import com.migestion.scheduling.dto.ExcepcionHorarioResponse;
import com.migestion.scheduling.dto.HorarioAtencionResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SchedulingMapper {

    @Mapping(target = "isActivo", source = "activo")
    HorarioAtencionResponse toResponse(HorarioAtencion horario);

    @Mapping(target = "isCerradoCompleto", source = "cerradoCompleto")
    ExcepcionHorarioResponse toResponse(ExcepcionHorario excepcion);

    @Mapping(target = "isRecurrente", source = "recurrente")
    BloqueoHorarioResponse toResponse(BloqueoHorario bloqueo);
}
