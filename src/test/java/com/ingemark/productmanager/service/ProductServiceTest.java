package com.ingemark.productmanager.service;

import com.ingemark.productmanager.exception.ProductNotFoundException;
import com.ingemark.productmanager.mapper.ProductMapper;
import com.ingemark.productmanager.model.product.Product;
import com.ingemark.productmanager.model.product.request.CreateProductDto;
import com.ingemark.productmanager.model.product.request.SearchProductDto;
import com.ingemark.productmanager.model.product.request.UpdateProductDto;
import com.ingemark.productmanager.model.product.response.PagedProductResponseDto;
import com.ingemark.productmanager.model.product.response.ProductResponseDto;
import com.ingemark.productmanager.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private CreateProductDto createProductDto;
    private UpdateProductDto updateProductDto;
    private ProductResponseDto productResponseDto;
    private SearchProductDto searchProductDto;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .code("TEST123456")
                .name("Test Product")
                .priceEur(new BigDecimal("100.00"))
                .priceUsd(new BigDecimal("110.00"))
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createProductDto = new CreateProductDto(
                "New Product",
                new BigDecimal("50.00"),
                true
        );

        updateProductDto = new UpdateProductDto(
                "Updated Product",
                new BigDecimal("75.00"),
                false
        );

        productResponseDto = new ProductResponseDto(
                "Test Product",
                "TEST123456",
                new BigDecimal("100.00"),
                new BigDecimal("110.00"),
                true
        );

        searchProductDto = new SearchProductDto(
                "Test",
                new BigDecimal("10.00"),
                new BigDecimal("200.00"),
                new BigDecimal("11.00"),
                new BigDecimal("220.00"),
                0,
                10,
                null,
                true
        );
    }

    @Test
    void searchProducts_WithSearchDto_ShouldReturnPagedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        PagedProductResponseDto expectedResponse = new PagedProductResponseDto(
                List.of(productResponseDto), 0, 10, 1L, 1
        );

        when(productRepository.findProductsByFilters(
                eq("Test"),
                eq(new BigDecimal("10.00")),
                eq(new BigDecimal("200.00")),
                eq(new BigDecimal("11.00")),
                eq(new BigDecimal("220.00")),
                any(Pageable.class)
        )).thenReturn(productPage);
        when(productMapper.toPagedProductResponseDto(productPage)).thenReturn(expectedResponse);

        // When
        PagedProductResponseDto result = productService.searchProducts(searchProductDto);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(productRepository).findProductsByFilters(
                eq("Test"),
                eq(new BigDecimal("10.00")),
                eq(new BigDecimal("200.00")),
                eq(new BigDecimal("11.00")),
                eq(new BigDecimal("220.00")),
                any(Pageable.class)
        );
        verify(productMapper).toPagedProductResponseDto(productPage);
    }

    @Test
    void searchProducts_WithNullSearchDto_ShouldReturnAllProductsWithDefaultPagination() {
        // Given
        Pageable defaultPageable = PageRequest.of(0, 5, Sort.by("name").ascending());
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), defaultPageable, 1);
        PagedProductResponseDto expectedResponse = new PagedProductResponseDto(
                List.of(productResponseDto), 0, 5, 1L, 1
        );

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toPagedProductResponseDto(productPage)).thenReturn(expectedResponse);

        // When
        PagedProductResponseDto result = productService.searchProducts(null);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(productRepository).findAll(any(Pageable.class));
        verify(productMapper).toPagedProductResponseDto(productPage);
        verify(productRepository, never()).findProductsByFilters(any(), any(), any(), any(), any(), any());
    }

    @Test
    void getProductByCode_WithValidCode_ShouldReturnProduct() {
        // Given
        when(productRepository.findByCode("TEST123456")).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponseDto(testProduct)).thenReturn(productResponseDto);

        // When
        ProductResponseDto result = productService.getProductByCode("TEST123456");

        // Then
        assertThat(result).isEqualTo(productResponseDto);
        verify(productRepository).findByCode("TEST123456");
        verify(productMapper).toResponseDto(testProduct);
    }

    @Test
    void getProductByCode_WithInvalidCode_ShouldThrowProductNotFoundException() {
        // Given
        when(productRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductByCode("INVALID"))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findByCode("INVALID");
        verify(productMapper, never()).toResponseDto(any());
    }

    @Test
    void createProduct_WithValidDto_ShouldCreateAndReturnProduct() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .priceEur(new BigDecimal("50.00"))
                .isAvailable(true)
                .build();

        Product savedProduct = Product.builder()
                .id(2L)
                .code("NEW1234567")
                .name("New Product")
                .priceEur(new BigDecimal("50.00"))
                .priceUsd(new BigDecimal("55.00"))
                .isAvailable(true)
                .build();

        ProductResponseDto expectedResponse = new ProductResponseDto(
                "New Product",
                "NEW1234567",
                new BigDecimal("50.00"),
                new BigDecimal("55.00"),
                true
        );

        when(productMapper.toEntity(createProductDto)).thenReturn(newProduct);
        when(currencyService.calculateUsdPrice(new BigDecimal("50.00"))).thenReturn(new BigDecimal("55.00"));
        when(productRepository.getNextCodeSequence()).thenReturn(123456789L);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toResponseDto(savedProduct)).thenReturn(expectedResponse);

        // When
        ProductResponseDto result = productService.createProduct(createProductDto);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(productMapper).toEntity(createProductDto);
        verify(currencyService).calculateUsdPrice(new BigDecimal("50.00"));
        verify(productRepository).getNextCodeSequence();
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponseDto(savedProduct);
    }

    @Test
    void updateProductByCode_WithValidCodeAndDto_ShouldUpdateAndReturnProduct() {
        // Given
        ProductResponseDto expectedResponse = new ProductResponseDto(
                "Updated Product",
                "TEST123456",
                new BigDecimal("75.00"),
                new BigDecimal("82.50"),
                false
        );

        when(productRepository.findByCode("TEST123456")).thenReturn(Optional.of(testProduct));
        when(currencyService.calculateUsdPrice(new BigDecimal("75.00"))).thenReturn(new BigDecimal("82.50"));
        when(productMapper.toResponseDto(testProduct)).thenReturn(expectedResponse);

        // When
        ProductResponseDto result = productService.updateProductByCode("TEST123456", updateProductDto);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(testProduct.getName()).isEqualTo("Updated Product");
        assertThat(testProduct.getPriceEur()).isEqualTo(new BigDecimal("75.00"));
        assertThat(testProduct.getPriceUsd()).isEqualTo(new BigDecimal("82.50"));
        assertThat(testProduct.getIsAvailable()).isFalse();

        verify(productRepository).findByCode("TEST123456");
        verify(currencyService).calculateUsdPrice(new BigDecimal("75.00"));
        verify(productMapper).toResponseDto(testProduct);
    }

    @Test
    void updateProductByCode_WithInvalidCode_ShouldThrowProductNotFoundException() {
        // Given
        when(productRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.updateProductByCode("INVALID", updateProductDto))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findByCode("INVALID");
        verify(currencyService, never()).calculateUsdPrice(any());
        verify(productMapper, never()).toResponseDto(any());
    }

    @Test
    void deleteProductByCode_WithValidCode_ShouldDeleteProduct() {
        // Given
        when(productRepository.findByCode("TEST123456")).thenReturn(Optional.of(testProduct));

        // When
        productService.deleteProductByCode("TEST123456");

        // Then
        verify(productRepository).findByCode("TEST123456");
        verify(productRepository).delete(testProduct);
    }

    @Test
    void deleteProductByCode_WithInvalidCode_ShouldThrowProductNotFoundException() {
        // Given
        when(productRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.deleteProductByCode("INVALID"))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findByCode("INVALID");
        verify(productRepository, never()).delete(any());
    }

    @Test
    void fromSequence_ShouldGeneratePaddedBase36Code() {
        // This tests the private method indirectly through createProduct
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .priceEur(new BigDecimal("50.00"))
                .isAvailable(true)
                .build();

        when(productMapper.toEntity(createProductDto)).thenReturn(newProduct);
        when(currencyService.calculateUsdPrice(any())).thenReturn(new BigDecimal("55.00"));
        when(productRepository.getNextCodeSequence()).thenReturn(123L); // Base36: 3F
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            // Verify the code format
            assertThat(product.getCode()).isEqualTo("000000003F");
            return product;
        });
        when(productMapper.toResponseDto(any())).thenReturn(productResponseDto);

        // When
        productService.createProduct(createProductDto);

        // Then - verification happens in the mock answer above
    }

    @Test
    void searchProducts_WithPartialSearchDto_ShouldUseDefaults() {
        // Given
        SearchProductDto partialDto = new SearchProductDto(
                null, null, null, null, null,
                null, null, null, false
        );

        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by("name").descending());
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), expectedPageable, 1);
        PagedProductResponseDto expectedResponse = new PagedProductResponseDto(
                List.of(productResponseDto), 0, 5, 1L, 1
        );

        when(productRepository.findProductsByFilters(
                isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        )).thenReturn(productPage);
        when(productMapper.toPagedProductResponseDto(productPage)).thenReturn(expectedResponse);

        // When
        PagedProductResponseDto result = productService.searchProducts(partialDto);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(productRepository).findProductsByFilters(
                isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        );
    }
}
