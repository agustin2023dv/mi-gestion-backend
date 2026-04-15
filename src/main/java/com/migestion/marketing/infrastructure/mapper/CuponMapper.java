package com.migestion.marketing.infrastructure.mapper;

import com.migestion.marketing.domain.Cupon;
import com.migestion.marketing.dto.CreateCuponRequest;
import com.migestion.marketing.dto.CuponResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CuponMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "codigo", source = "codigo")
    @Mapping(target = "tipoDescuento", source = "tipoDescuento")
    @Mapping(target = "valorDescuento", source = "valorDescuento")
    @Mapping(target = "usosMaximos", source = "usosMaximos")
    @Mapping(target = "fechaInicio", source = "fechaInicio")
    @Mapping(target = "fechaFin", source = "fechaFin")
    @Mapping(target = "montoMinimo", source = "montoMinimo")
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "usosActuales", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Cupon toEntity(CreateCuponRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "codigo", source = "codigo")
    @Mapping(target = "tipoDescuento", source = "tipoDescuento")
    @Mapping(target = "valorDescuento", source = "valorDescuento")
    @Mapping(target = "usosMaximos", source = "usosMaximos")
    @Mapping(target = "usosActuales", source = "usosActuales")
    @Mapping(target = "fechaInicio", source = "fechaInicio")
    @Mapping(target = "fechaFin", source = "fechaFin")
    @Mapping(target = "montoMinimo", source = "montoMinimo")
    @Mapping(target = "isActive", expression = "java(cupon.isActive())")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    CuponResponse toResponse(Cupon cupon);
}
