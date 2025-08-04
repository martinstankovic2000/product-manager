package com.ingemark.productmanager.mapper;

import com.ingemark.productmanager.model.product.request.CreateProductDto;
import com.ingemark.productmanager.model.product.response.PagedProductResponseDto;
import com.ingemark.productmanager.model.product.Product;
import com.ingemark.productmanager.model.product.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper class responsible for converting between Product entities and DTOs.
 */
@Component
public class ProductMapper {

    /**
     * Converts a CreateProductDto to a Product entity.
     *
     * @param createDto the DTO containing product creation data
     * @return a new Product entity populated from the DTO
     */
    public Product toEntity(CreateProductDto createDto) {
        return Product.builder()
                .name(createDto.name())
                .priceEur(createDto.priceEur())
                .isAvailable(createDto.isAvailable())
                .build();
    }

    /**
     * Converts a Product entity to a ProductResponseDto.
     *
     * @param product the Product entity to convert
     * @return a ProductResponseDto representing the entity
     */
    public ProductResponseDto toResponseDto(Product product) {
        return new ProductResponseDto(
                product.getName(),
                product.getCode(),
                product.getPriceEur(),
                product.getPriceUsd(),
                product.getIsAvailable()
        );

    }

    /**
     * Converts a Page of Product entities to a paged response DTO.
     *
     * @param products a Page of Product entities
     * @return a PagedProductResponseDto containing the mapped products and pagination info
     */
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
