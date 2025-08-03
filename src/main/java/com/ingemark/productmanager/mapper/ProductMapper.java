package com.ingemark.productmanager.mapper;

import com.ingemark.productmanager.model.CreateProductDto;
import com.ingemark.productmanager.model.PagedProductResponseDto;
import com.ingemark.productmanager.model.Product;
import com.ingemark.productmanager.model.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductDto createDto) {
        return Product.builder()
                .name(createDto.name())
                .priceEur(createDto.priceEur())
                .isAvailable(createDto.isAvailable())
                .build();
    }

    public ProductResponseDto toResponseDto(Product product) {
        return new ProductResponseDto(
                product.getName(),
                product.getCode(),
                product.getPriceEur(),
                product.getPriceUsd(),
                product.getIsAvailable()
        );

    }

    public PagedProductResponseDto toPagedProductResponseDto(Page<Product> products) {
        List<ProductResponseDto> content = products.getContent()
                .stream()
                .map(this::toResponseDto)
                .toList();

        return new PagedProductResponseDto(
                content,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages());
    }
}
