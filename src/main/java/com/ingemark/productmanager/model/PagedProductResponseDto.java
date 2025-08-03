package com.ingemark.productmanager.model;

import java.util.List;

public record PagedProductResponse(
        List<ProductResponseDto> productResponseDto,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {
}
