package com.ingemark.productmanager.service;

import com.ingemark.productmanager.exception.ProductNotFoundException;
import com.ingemark.productmanager.mapper.ProductMapper;
import com.ingemark.productmanager.model.*;
import com.ingemark.productmanager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ingemark.productmanager.model.SearchProductDto.DEFAULT_PAGE;
import static com.ingemark.productmanager.model.SearchProductDto.DEFAULT_SIZE;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Transactional(readOnly = true)
    public PagedProductResponseDto searchProducts(SearchProductDto searchProductDto) {
        if (nonNull(searchProductDto)) {
            Pageable pageable = resolvePageable(searchProductDto);
            Page<Product> searchedProducts = productRepository.findAll(pageable);

            return productMapper.toPagedProductResponseDto(searchedProducts);
        }
        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by(ProductSortField.NAME.name()).ascending());
        Page<Product> searchedProducts = productRepository.findAll(pageable);

        return productMapper.toPagedProductResponseDto(searchedProducts);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(code));
        return productMapper.toResponseDto(product);
    }

    @Transactional
    public ProductResponseDto createProduct(CreateProductDto createProductDto) {
        Product product = productMapper.toEntity(createProductDto);
        //TODO Calculate USD price using HNB API
        //product.setPriceUsd();
        product.setCode(fromSequence(productRepository.getNextCodeSequence()));
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDto(savedProduct);
    }

    /**
     * Converts a sequence value to a padded, uppercase Base36 code.
     *
     * @param sequenceValue The numeric sequence value (from DB)
     * @return A 10-character unique product code
     */
    private String fromSequence(long sequenceValue) {
        String base36 = Long.toString(sequenceValue, 36).toUpperCase();
        return String.format("%10s", base36).replace(' ', '0');
    }

    private Pageable resolvePageable(SearchProductDto searchProductDto) {
        String sortBy = nonNull(searchProductDto.sortBy()) ? searchProductDto.sortBy().name() : ProductSortField.NAME.name();
        Sort sort = searchProductDto.sortAscending() ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        int page = nonNull(searchProductDto.page()) ? searchProductDto.page() : DEFAULT_PAGE;
        int size = nonNull(searchProductDto.size()) ? searchProductDto.size() : DEFAULT_SIZE;

        return PageRequest.of(page, size, sort);
    }
}
