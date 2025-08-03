package com.ingemark.productmanager.controller;

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



@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductDto createProductDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<ProductResponseDto> getProductByCode(@PathVariable String code) {

        return ResponseEntity.ok(productService.getProductByCode(code));
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<PagedProductResponseDto> searchProducts(@Valid @RequestBody(required = false) SearchProductDto searchDto) {

        return ResponseEntity.ok(productService.searchProducts(searchDto));
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProductByCode(@PathVariable String code, @Valid @RequestBody UpdateProductDto updateProductDto) {

        return ResponseEntity.ok(productService.updateProductByCode(code, updateProductDto));
    }

    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProductByCode(@PathVariable String code) {

        productService.deleteProductByCode(code);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
