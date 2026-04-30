package com.migestion.platform.infrastructure.mapper;

import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.dto.PlanSuscripcionResponse;
import com.migestion.platform.dto.TenantResponse;
import com.migestion.tenant.domain.Tenant;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PlatformMapper {

    PlanSuscripcionResponse toPlanSuscripcionResponse(PlanSuscripcion planSuscripcion);

    @Mapping(target = "id", source = "tenant.id")
    @Mapping(target = "tenantId", source = "tenant.tenantIdentifier")
    @Mapping(target = "nombreNegocio", source = "tenant.nombreNegocio")
    @Mapping(target = "slug", source = "tenant.slug")
    @Mapping(target = "planSuscripcion", source = "planSuscripcion")
    @Mapping(target = "propietario", source = "propietario")
    @Mapping(target = "logoUrl", source = "tenant.logoUrl")
    @Mapping(target = "colorPrimario", source = "tenant.colorPrimario")
    @Mapping(target = "colorSecundario", source = "tenant.colorSecundario")
    @Mapping(target = "visibilidadPublica", source = "tenant.visibilidadPublica")
    @Mapping(target = "aceptaReservasServicios", source = "tenant.aceptaReservasServicios")
    @Mapping(target = "permitePedidosProgramados", source = "tenant.permitePedidosProgramados")
    @Mapping(target = "isActive", source = "tenant.active")
    @Mapping(target = "isSuspended", source = "tenant.suspended")
    @Mapping(target = "createdAt", source = "tenant.createdAt")
    @Mapping(target = "updatedAt", source = "tenant.updatedAt")
    TenantResponse toTenantResponse(
            Tenant tenant,
            PlanSuscripcion planSuscripcion,
            TenantResponse.PropietarioResponse propietario
    );

    @Mapping(target = "id", source = "tenant.id")
    @Mapping(target = "tenantId", source = "tenant.tenantIdentifier")
    @Mapping(target = "nombreNegocio", source = "tenant.nombreNegocio")
    @Mapping(target = "slug", source = "tenant.slug")
    @Mapping(target = "planSuscripcion", ignore = true)
    @Mapping(target = "propietario", ignore = true)
    @Mapping(target = "logoUrl", source = "tenant.logoUrl")
    @Mapping(target = "colorPrimario", source = "tenant.colorPrimario")
    @Mapping(target = "colorSecundario", source = "tenant.colorSecundario")
    @Mapping(target = "visibilidadPublica", source = "tenant.visibilidadPublica")
    @Mapping(target = "aceptaReservasServicios", source = "tenant.aceptaReservasServicios")
    @Mapping(target = "permitePedidosProgramados", source = "tenant.permitePedidosProgramados")
    @Mapping(target = "isActive", source = "tenant.active")
    @Mapping(target = "isSuspended", source = "tenant.suspended")
    @Mapping(target = "createdAt", source = "tenant.createdAt")
    @Mapping(target = "updatedAt", source = "tenant.updatedAt")
    TenantResponse toTenantResponse(Tenant tenant);
}
