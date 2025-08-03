package com.ingemark.productmanager.service;

import com.ingemark.productmanager.exception.ProductNotFoundException;
import com.ingemark.productmanager.mapper.ProductMapper;
import com.ingemark.productmanager.model.product.Product;
import com.ingemark.productmanager.model.product.ProductSortField;
import com.ingemark.productmanager.model.product.request.CreateProductDto;
import com.ingemark.productmanager.model.product.request.SearchProductDto;
import com.ingemark.productmanager.model.product.request.UpdateProductDto;
import com.ingemark.productmanager.model.product.response.PagedProductResponseDto;
import com.ingemark.productmanager.model.product.response.ProductResponseDto;
import com.ingemark.productmanager.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ingemark.productmanager.model.product.request.SearchProductDto.DEFAULT_PAGE;
import static com.ingemark.productmanager.model.product.request.SearchProductDto.DEFAULT_SIZE;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CurrencyService currencyService;


    @Transactional(readOnly = true)
    public PagedProductResponseDto searchProducts(SearchProductDto searchProductDto) {
        if (nonNull(searchProductDto)) {
            Pageable pageable = resolvePageable(searchProductDto);
            Page<Product> searchedProducts = productRepository.findProductsByFilters(
                    searchProductDto.name(),
                    searchProductDto.minPriceEur(),
                    searchProductDto.maxPriceEur(),
                    searchProductDto.minPriceUsd(),
                    searchProductDto.maxPriceUsd(),
                    pageable
            );

            return productMapper.toPagedProductResponseDto(searchedProducts);
        }
        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, Sort.by(ProductSortField.NAME.getField()).ascending());
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
        product.setPriceUsd(currencyService.calculateUsdPrice(createProductDto.priceEur()));
        product.setCode(fromSequence(productRepository.getNextCodeSequence()));
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDto(savedProduct);
    }

    @Transactional
    public ProductResponseDto updateProductByCode(String code, @Valid UpdateProductDto updateProductDto) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(code));
        product.setName(updateProductDto.name());
        product.setPriceEur(updateProductDto.priceEur());
        product.setPriceUsd(currencyService.calculateUsdPrice(updateProductDto.priceEur()));
        product.setIsAvailable(updateProductDto.isAvailable());
        return productMapper.toResponseDto(product);
    }

    @Transactional
    public void deleteProductByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(code));
        productRepository.delete(product);
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
        String sortBy = nonNull(searchProductDto.sortBy()) ? searchProductDto.sortBy().getField() : ProductSortField.NAME.getField();
        Sort sort = searchProductDto.sortAscending() ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        int page = nonNull(searchProductDto.page()) ? searchProductDto.page() : DEFAULT_PAGE;
        int size = nonNull(searchProductDto.size()) ? searchProductDto.size() : DEFAULT_SIZE;

        return PageRequest.of(page, size, sort);
    }
}
