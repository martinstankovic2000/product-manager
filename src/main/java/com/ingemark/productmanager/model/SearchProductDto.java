package com.ingemark.productmanager.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record SearchProductDto (
        String name,
        BigDecimal minPriceEur,
        BigDecimal maxPriceEur,
        BigDecimal minPriceUsd,
        BigDecimal maxPriceUsd,
        @Min(value = 0, message = "{product.search.page.min}")
        Integer page,
        @Min(value = 1, message = "{product.search.size.min}")
        @Max(value = 20, message = "{product.search.size.max}")
        Integer size,
        ProductSortField sortBy,
        boolean sortAscending
){
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 5;
}
