package com.migestion.marketing.application;

import com.migestion.marketing.domain.ResenaProductoRepository;
import com.migestion.marketing.dto.ReviewResponse;
import com.migestion.marketing.infrastructure.mapper.ReviewMapper;
import com.migestion.platform.dto.PageResponse;
import com.migestion.shared.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPendingReviewsUseCase {

    private final ResenaProductoRepository resenaProductoRepository;
    private final ReviewMapper reviewMapper;

    public PageResponse<ReviewResponse> execute(Pageable pageable) {
        Long tenantId = requireTenantId();

        // Get pending product reviews
        Page<ReviewResponse> productReviews = resenaProductoRepository
                .findByTenantIdAndRequiereModeracionTrue(tenantId, pageable)
                .map(reviewMapper::toReviewResponse);

        return toPageResponse(productReviews);
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

    private Long requireTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID not found in context");
        }
        return tenantId;
    }
}
