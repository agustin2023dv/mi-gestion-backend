package com.migestion.catalog.infrastructure;

import com.migestion.catalog.domain.Categoria;
import com.migestion.catalog.domain.Subcategoria;
import com.migestion.catalog.dto.CategoriaDetailResponse;
import com.migestion.catalog.dto.CategoriaListItemResponse;
import com.migestion.catalog.dto.SubcategoriaSummaryResponse;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CategoriaMapper {

    @Mapping(target = "isActive", source = "active")
    CategoriaListItemResponse toListItemResponse(Categoria categoria);

    @Mapping(target = "id", source = "categoria.id")
    @Mapping(target = "nombre", source = "categoria.nombre")
    @Mapping(target = "descripcion", source = "categoria.descripcion")
    @Mapping(target = "isActive", source = "categoria.active")
    @Mapping(target = "subcategorias", source = "subcategorias")
    @Mapping(target = "createdAt", source = "categoria.createdAt")
    @Mapping(target = "updatedAt", source = "categoria.updatedAt")
    CategoriaDetailResponse toDetailResponse(Categoria categoria, List<SubcategoriaSummaryResponse> subcategorias);

    @Mapping(target = "isActive", source = "active")
    SubcategoriaSummaryResponse toSubcategoriaSummaryResponse(Subcategoria subcategoria);
}