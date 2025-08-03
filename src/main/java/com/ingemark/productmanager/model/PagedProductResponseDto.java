package com.ingemark.productmanager.model;

import java.util.List;

public record PagedProductResponseDto(
        List<ProductResponseDto> productResponseDtos,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {
}
