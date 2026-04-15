package com.migestion.notifications.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
}
