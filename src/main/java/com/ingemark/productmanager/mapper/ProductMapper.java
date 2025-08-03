package com.ingemark.productmanager.mapper;

import com.ingemark.productmanager.model.CreateProductDto;
import com.ingemark.productmanager.model.PagedProductResponseDto;
import com.ingemark.productmanager.model.Product;
import com.ingemark.productmanager.model.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "priceUsd", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductDto createDto);

    ProductResponseDto toResponseDto(Product product);

    default PagedProductResponseDto toPagedProductResponseDto(Page<Product> products) {
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
