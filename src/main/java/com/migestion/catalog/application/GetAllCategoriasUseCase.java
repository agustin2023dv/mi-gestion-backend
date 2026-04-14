package com.migestion.catalog.application;

import com.migestion.catalog.domain.CategoriaRepository;
import com.migestion.catalog.dto.CategoriaListItemResponse;
import com.migestion.catalog.dto.PageResponse;
import com.migestion.catalog.infrastructure.CategoriaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllCategoriasUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public GetAllCategoriasUseCase(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<CategoriaListItemResponse> execute(Pageable pageable) {
        Page<CategoriaListItemResponse> categoriaPage = categoriaRepository.findByIsActiveTrue(pageable)
                .map(categoriaMapper::toListItemResponse);
        return toPageResponse(categoriaPage);
    }

    private PageResponse<CategoriaListItemResponse> toPageResponse(Page<CategoriaListItemResponse> page) {
        return PageResponse.<CategoriaListItemResponse>builder()
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