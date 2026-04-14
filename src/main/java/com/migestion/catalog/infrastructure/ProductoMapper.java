package com.migestion.catalog.infrastructure;

import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.dto.CreateProductoRequest;
import com.migestion.catalog.dto.ProductoResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ProductoMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "subcategoriaId", source = "subcategoriaId")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "precio", source = "precio")
    @Mapping(target = "costoUnitarioManualOverride", source = "costoUnitarioManualOverride")
    @Mapping(target = "stock", source = "stock")
    @Mapping(target = "stockMinimo", source = "stockMinimo")
    @Mapping(target = "imagenUrl", source = "imagenUrl")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "duracionMinutos", source = "duracionMinutos")
    @Mapping(target = "bufferEntreTurnosMin", source = "bufferEntreTurnosMin")
    @Mapping(target = "edadMinima", source = "edadMinima")
    @Mapping(target = "costoUnitarioCalculado", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Producto toEntity(CreateProductoRequest request);

    @Mapping(target = "isActive", source = "active")
    ProductoResponse toResponse(Producto producto);
}