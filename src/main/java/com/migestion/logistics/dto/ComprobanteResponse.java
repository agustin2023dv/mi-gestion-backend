package com.migestion.logistics.dto;

import lombok.Builder;

@Builder
public record ComprobanteResponse(
        String pdfUrl,
        String hashCriptografico
) {
}
