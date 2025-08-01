package com.ingemark.productmanager.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductDto(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Price EUR is required")
        @DecimalMin(value = "0.0", message = "Price EUR must be >= 0")
        BigDecimal priceEur,

        @NotNull(message = "Availability status is required")
        Boolean isAvailable
){}
