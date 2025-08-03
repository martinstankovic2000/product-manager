package com.ingemark.productmanager.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductDto(
        @NotBlank(message = "{product.name.blank}")
        String name,

        @NotNull(message = "{product.price.required}")
        @DecimalMin(value = "0.0", message = "{product.price.negative}")
        BigDecimal priceEur,

        @NotNull(message = "{product.availability.required}")
        Boolean isAvailable
){}
