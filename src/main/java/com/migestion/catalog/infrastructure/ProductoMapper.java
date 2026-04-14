package com.migestion.catalog.infrastructure;

import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.dto.CreateProductoRequest;
import com.migestion.catalog.dto.ProductoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "costoUnitarioCalculado", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Producto toEntity(CreateProductoRequest request);

    ProductoResponse toResponse(Producto producto);
}