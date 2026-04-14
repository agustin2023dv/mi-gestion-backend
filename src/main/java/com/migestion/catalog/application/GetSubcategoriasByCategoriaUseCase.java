package com.migestion.catalog.application;

import com.migestion.catalog.domain.CategoriaRepository;
import com.migestion.catalog.domain.SubcategoriaRepository;
import com.migestion.catalog.dto.PageResponse;
import com.migestion.catalog.dto.SubcategoriaResponse;
import com.migestion.catalog.infrastructure.SubcategoriaMapper;
import com.migestion.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetSubcategoriasByCategoriaUseCase {

    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final SubcategoriaMapper subcategoriaMapper;

    public GetSubcategoriasByCategoriaUseCase(
            CategoriaRepository categoriaRepository,
            SubcategoriaRepository subcategoriaRepository,
            SubcategoriaMapper subcategoriaMapper
    ) {
        this.categoriaRepository = categoriaRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.subcategoriaMapper = subcategoriaMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<SubcategoriaResponse> execute(Long categoriaId, Pageable pageable) {
        if (!categoriaRepository.existsByIdAndIsActiveTrue(categoriaId)) {
            throw new ResourceNotFoundException("Categoria", categoriaId);
        }

        Page<SubcategoriaResponse> subcategoriaPage = subcategoriaRepository
                .findByCategoriaIdAndIsActiveTrue(categoriaId, pageable)
                .map(subcategoriaMapper::toResponse);

        return toPageResponse(subcategoriaPage);
    }

    private PageResponse<SubcategoriaResponse> toPageResponse(Page<SubcategoriaResponse> page) {
        return PageResponse.<SubcategoriaResponse>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}