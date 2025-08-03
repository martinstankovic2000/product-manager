package com.ingemark.productmanager.model;

import java.math.BigDecimal;

public record ProductResponseDto(
        String name,
        String code,
        BigDecimal priceEur,
        BigDecimal priceUsd,
        Boolean isAvailable
) {
}
