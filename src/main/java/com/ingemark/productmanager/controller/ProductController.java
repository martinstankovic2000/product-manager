package com.ingemark.productmanager.controller;

import com.ingemark.productmanager.exception.ProductNotFoundException;
import com.ingemark.productmanager.model.product.request.CreateProductDto;
import com.ingemark.productmanager.model.product.request.UpdateProductDto;
import com.ingemark.productmanager.model.product.response.PagedProductResponseDto;
import com.ingemark.productmanager.model.product.response.ProductResponseDto;
import com.ingemark.productmanager.model.product.request.SearchProductDto;
import com.ingemark.productmanager.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing products.
 * Provides endpoints to create, update, delete, and retrieve products.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a new product.
     *
     * @param createProductDto DTO with product creation details.
     * @return Created product details.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductDto createProductDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    /**
     * Retrieves a product by its unique code.
     *
     * @param code The unique product code.
     * @return Product details.
     * @throws ProductNotFoundException if product with code does not exist.
     */
    @GetMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<ProductResponseDto> getProductByCode(@PathVariable String code) {

        return ResponseEntity.ok(productService.getProductByCode(code));
    }

    /**
     * Searches products with optional filters and pagination.
     *
     * @param searchDto DTO containing search filters and pagination info.
     * @return Paged list of matching products.
     */
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<PagedProductResponseDto> searchProducts(@Valid @RequestBody(required = false) SearchProductDto searchDto) {

        return ResponseEntity.ok(productService.searchProducts(searchDto));
    }

    /**
     * Updates an existing product by code.
     *
     * @param code The unique code of the product to update.
     * @param updateProductDto DTO with updated product details.
     * @return Updated product details.
     * @throws ProductNotFoundException if product with code does not exist.
     */
    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProductByCode(@PathVariable String code, @Valid @RequestBody UpdateProductDto updateProductDto) {

        return ResponseEntity.ok(productService.updateProductByCode(code, updateProductDto));
    }

    /**
     * Deletes a product by its code.
     *
     * @param code The unique code of the product to delete.
     * @throws ProductNotFoundException if product with code does not exist.
     */
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProductByCode(@PathVariable String code) {

        productService.deleteProductByCode(code);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
