package com.ingemark.productmanager.mapper;

import com.ingemark.productmanager.model.CreateProductDto;
import com.ingemark.productmanager.model.Product;
import com.ingemark.productmanager.model.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "priceUsd", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductDto createDto);

    ProductResponseDto toResponseDto(Product product);
}
