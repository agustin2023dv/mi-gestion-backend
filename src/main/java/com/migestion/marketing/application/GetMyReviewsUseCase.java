package com.migestion.marketing.application;

import com.migestion.marketing.domain.ResenaProducto;
import com.migestion.marketing.domain.ResenaProductoRepository;
import com.migestion.marketing.domain.ResenaTenant;
import com.migestion.marketing.domain.ResenaTenantRepository;
import com.migestion.marketing.dto.ReviewResponse;
import com.migestion.marketing.infrastructure.mapper.ReviewMapper;
import com.migestion.platform.dto.PageResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyReviewsUseCase {

    private final ResenaProductoRepository resenaProductoRepository;
    private final ResenaTenantRepository resenaTenantRepository;
    private final ReviewMapper reviewMapper;

    public PageResponse<ReviewResponse> execute(Long clienteId, Pageable pageable) {
        // Get all reviews from this client (both product and tenant reviews)
        // Note: Since we can't easily combine two different Page results efficiently,
        // we'll fetch the data and combine manually
        
        // For simplicity in single-page queries, fetch both types
        List<ReviewResponse> allReviews = new ArrayList<>();
        
        // Fetch product reviews
        List<ResenaProducto> productoReviews = resenaProductoRepository.findByClienteId(clienteId);
        productoReviews.stream()
                .map(reviewMapper::toReviewResponse)
                .forEach(allReviews::add);
        
        // Fetch tenant reviews
        List<ResenaTenant> tenantReviews = resenaTenantRepository.findByClienteId(clienteId);
        tenantReviews.stream()
                .map(reviewMapper::toReviewResponse)
                .forEach(allReviews::add);
        
        // Sort by createdAt descending
        allReviews.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        
        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allReviews.size());
        
        List<ReviewResponse> pageContent = allReviews.subList(start, end);
        Page<ReviewResponse> page = new PageImpl<>(pageContent, pageable, allReviews.size());
        
        return toPageResponse(page);
    }

    private PageResponse<ReviewResponse> toPageResponse(Page<ReviewResponse> page) {
        return PageResponse.<ReviewResponse>builder()
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
