package com.ingemark.productmanager.controller;

import com.ingemark.productmanager.model.request.CreateProductDto;

import com.ingemark.productmanager.model.request.UpdateProductDto;
import com.ingemark.productmanager.model.response.PagedProductResponseDto;
import com.ingemark.productmanager.model.response.ProductResponseDto;
import com.ingemark.productmanager.model.request.SearchProductDto;
import com.ingemark.productmanager.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductDto createProductDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ProductResponseDto> getProductByCode(@PathVariable String code) {

        return ResponseEntity.ok(productService.getProductByCode(code));
    }

    @PostMapping("/search")
    public ResponseEntity<PagedProductResponseDto> searchProducts(@Valid @RequestBody(required = false) SearchProductDto searchDto) {

        return ResponseEntity.ok(productService.searchProducts(searchDto));
    }

    @PutMapping("/{code}")
    public ResponseEntity<ProductResponseDto> updateProductByCode(@PathVariable String code, @Valid @RequestBody UpdateProductDto updateProductDto) {

        return ResponseEntity.ok(productService.updateProductByCode(code, updateProductDto));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteProductByCode(@PathVariable String code) {

        productService.deleteProductByCode(code);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
