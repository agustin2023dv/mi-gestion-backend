package com.migestion.catalog.application;

import com.migestion.catalog.domain.Categoria;
import com.migestion.catalog.domain.CategoriaRepository;
import com.migestion.catalog.dto.CategoriaDetailResponse;
import com.migestion.catalog.dto.SubcategoriaSummaryResponse;
import com.migestion.catalog.infrastructure.CategoriaMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCategoriaByIdUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public GetCategoriaByIdUseCase(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Transactional(readOnly = true)
    public CategoriaDetailResponse execute(Long categoriaId) {
        Categoria categoria = categoriaRepository.findByIdAndIsActiveTrue(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", categoriaId));

        List<SubcategoriaSummaryResponse> subcategorias = categoria.getSubcategorias().stream()
                .filter(subcategoria -> subcategoria.isActive())
                .map(categoriaMapper::toSubcategoriaSummaryResponse)
                .toList();

        return categoriaMapper.toDetailResponse(categoria, subcategorias);
    }
}