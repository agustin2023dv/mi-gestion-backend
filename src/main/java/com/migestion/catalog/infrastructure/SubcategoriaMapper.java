package com.migestion.catalog.infrastructure;

import com.migestion.catalog.domain.Categoria;
import com.migestion.catalog.domain.Subcategoria;
import com.migestion.catalog.dto.CategoriaReferenceResponse;
import com.migestion.catalog.dto.SubcategoriaResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SubcategoriaMapper {

    CategoriaReferenceResponse toCategoriaReferenceResponse(Categoria categoria);

    @Mapping(target = "categoria", source = "categoria")
    @Mapping(target = "isActive", source = "active")
    SubcategoriaResponse toResponse(Subcategoria subcategoria);
}