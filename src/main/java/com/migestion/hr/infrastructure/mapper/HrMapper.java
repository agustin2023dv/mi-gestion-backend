package com.migestion.hr.infrastructure.mapper;

import com.migestion.hr.domain.Empleado;
import com.migestion.hr.domain.NominaEmpleado;
import com.migestion.hr.dto.EmpleadoResponse;
import com.migestion.hr.dto.NominaEmpleadoResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface HrMapper {

    @Mapping(target = "isActive", source = "active")
    EmpleadoResponse toResponse(Empleado empleado);

    @Mapping(target = "isPagado", source = "pagado")
    NominaEmpleadoResponse toResponse(NominaEmpleado nomina);
}
