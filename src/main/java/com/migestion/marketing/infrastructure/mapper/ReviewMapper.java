package com.migestion.marketing.infrastructure.mapper;

import com.migestion.marketing.domain.ResenaProducto;
import com.migestion.marketing.domain.ResenaTenant;
import com.migestion.marketing.dto.CreateReviewRequest;
import com.migestion.marketing.dto.CreateTenantReviewRequest;
import com.migestion.marketing.dto.ReviewResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ReviewMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "productoId", source = "productoId")
    @Mapping(target = "pedidoId", source = "pedidoId")
    @Mapping(target = "turnoId", ignore = true)
    @Mapping(target = "empleadoId", ignore = true)
    @Mapping(target = "puntuacion", source = "puntuacion")
    @Mapping(target = "comentario", source = "comentario")
    @Mapping(target = "esVerificada", ignore = true)
    @Mapping(target = "requiereModeracion", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "respuestaEmprendedor", ignore = true)
    @Mapping(target = "respuestaFecha", ignore = true)
    @Mapping(target = "motivoRechazo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    ResenaProducto toResenaProductoEntity(CreateReviewRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "pedidoId", source = "pedidoId")
    @Mapping(target = "turnoId", ignore = true)
    @Mapping(target = "puntuacion", source = "puntuacion")
    @Mapping(target = "comentario", source = "comentario")
    @Mapping(target = "esVerificada", ignore = true)
    @Mapping(target = "requiereModeracion", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "respuestaEmprendedor", ignore = true)
    @Mapping(target = "respuestaFecha", ignore = true)
    @Mapping(target = "motivoRechazo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    ResenaTenant toResenaTenantEntity(CreateReviewRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "pedidoId", source = "pedidoId")
    @Mapping(target = "turnoId", source = "turnoId")
    @Mapping(target = "puntuacion", source = "puntuacion")
    @Mapping(target = "comentario", source = "comentario")
    @Mapping(target = "esVerificada", ignore = true)
    @Mapping(target = "requiereModeracion", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "respuestaEmprendedor", ignore = true)
    @Mapping(target = "respuestaFecha", ignore = true)
    @Mapping(target = "motivoRechazo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    ResenaTenant toResenaTenantEntity(CreateTenantReviewRequest request);

    ReviewResponse toReviewResponse(ResenaProducto entity);

    ReviewResponse toReviewResponse(ResenaTenant entity);
}

