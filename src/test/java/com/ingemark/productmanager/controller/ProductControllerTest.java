package com.ingemark.productmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.productmanager.exception.ProductNotFoundException;
import com.ingemark.productmanager.model.product.request.CreateProductDto;
import com.ingemark.productmanager.model.product.request.SearchProductDto;
import com.ingemark.productmanager.model.product.request.UpdateProductDto;
import com.ingemark.productmanager.model.product.response.PagedProductResponseDto;
import com.ingemark.productmanager.model.product.response.ProductResponseDto;
import com.ingemark.productmanager.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateProductDto createProductDto;
    private UpdateProductDto updateProductDto;
    private ProductResponseDto productResponseDto;
    private SearchProductDto searchProductDto;
    private PagedProductResponseDto pagedProductResponseDto;

    @BeforeEach
    void setUp() {
        createProductDto = new CreateProductDto(
                "Test Product",
                new BigDecimal("100.00"),
                true
        );

        updateProductDto = new UpdateProductDto(
                "Updated Product",
                new BigDecimal("150.00"),
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

        pagedProductResponseDto = new PagedProductResponseDto(
                List.of(productResponseDto),
                0,
                10,
                1L,
                1
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WithValidDto_ShouldReturnCreatedProduct() throws Exception {
        // Given
        when(productService.createProduct(any(CreateProductDto.class))).thenReturn(productResponseDto);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProductDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.code").value("TEST123456"))
                .andExpect(jsonPath("$.priceEur").value(100.00))
                .andExpect(jsonPath("$.priceUsd").value(110.00))
                .andExpect(jsonPath("$.isAvailable").value(true));

        verify(productService).createProduct(any(CreateProductDto.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createProduct_WithCustomerRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProductDto)))
                .andExpect(status().isForbidden());

        verify(productService, never()).createProduct(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WithInvalidDto_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateProductDto invalidDto = new CreateProductDto(
                "", // blank name
                new BigDecimal("-10.00"), // negative price
                null // null availability
        );

        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductByCode_WithValidCode_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductByCode("TEST123456")).thenReturn(productResponseDto);

        // When & Then
        mockMvc.perform(get("/api/products/TEST123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.code").value("TEST123456"))
                .andExpect(jsonPath("$.priceEur").value(100.00))
                .andExpect(jsonPath("$.priceUsd").value(110.00))
                .andExpect(jsonPath("$.isAvailable").value(true));

        verify(productService).getProductByCode("TEST123456");
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getProductByCode_WithCustomerRole_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductByCode("TEST123456")).thenReturn(productResponseDto);

        // When & Then
        mockMvc.perform(get("/api/products/TEST123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService).getProductByCode("TEST123456");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProductByCode_WithInvalidCode_ShouldReturnNotFound() throws Exception {
        // Given
        when(productService.getProductByCode("INVALID")).thenThrow(new ProductNotFoundException("INVALID"));

        // When & Then
        mockMvc.perform(get("/api/products/INVALID"))
                .andExpect(status().isNotFound());

        verify(productService).getProductByCode("INVALID");
    }

    @Test
    void getProductByCode_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/products/TEST123456"))
                .andExpect(status().isUnauthorized());

        verify(productService, never()).getProductByCode(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchProducts_WithValidDto_ShouldReturnPagedResults() throws Exception {
        // Given
        when(productService.searchProducts(any(SearchProductDto.class))).thenReturn(pagedProductResponseDto);

        // When & Then
        mockMvc.perform(post("/api/products/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productResponseDtos").isArray())
                .andExpect(jsonPath("$.productResponseDtos[0].name").value("Test Product"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(productService).searchProducts(any(SearchProductDto.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void searchProducts_WithCustomerRole_ShouldReturnResults() throws Exception {
        // Given
        when(productService.searchProducts(any(SearchProductDto.class))).thenReturn(pagedProductResponseDto);

        // When & Then
        mockMvc.perform(post("/api/products/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchProductDto)))
                .andExpect(status().isOk());

        verify(productService).searchProducts(any(SearchProductDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchProducts_WithNullBody_ShouldReturnDefaultResults() throws Exception {
        // Given
        when(productService.searchProducts(isNull())).thenReturn(pagedProductResponseDto);

        // When & Then
        mockMvc.perform(post("/api/products/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService).searchProducts(isNull());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchProducts_WithInvalidDto_ShouldReturnBadRequest() throws Exception {
        // Given
        SearchProductDto invalidDto = new SearchProductDto(
                null, null, null, null, null,
                -1, // invalid page
                25, // invalid size (too large)
                null, true
        );

        // When & Then
        mockMvc.perform(post("/api/products/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).searchProducts(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProductByCode_WithValidCodeAndDto_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        ProductResponseDto updatedResponse = new ProductResponseDto(
                "Updated Product",
                "TEST123456",
                new BigDecimal("150.00"),
                new BigDecimal("165.00"),
                false
        );
        when(productService.updateProductByCode(eq("TEST123456"), any(UpdateProductDto.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/products/TEST123456")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.code").value("TEST123456"))
                .andExpect(jsonPath("$.priceEur").value(150.00))
                .andExpect(jsonPath("$.priceUsd").value(165.00))
                .andExpect(jsonPath("$.isAvailable").value(false));

        verify(productService).updateProductByCode(eq("TEST123456"), any(UpdateProductDto.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateProductByCode_WithCustomerRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/products/TEST123456")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProductDto)))
                .andExpect(status().isForbidden());

        verify(productService, never()).updateProductByCode(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProductByCode_WithInvalidCode_ShouldReturnNotFound() throws Exception {
        // Given
        when(productService.updateProductByCode(eq("INVALID"), any(UpdateProductDto.class)))
                .thenThrow(new ProductNotFoundException("INVALID"));

        // When & Then
        mockMvc.perform(put("/api/products/INVALID")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProductDto)))
                .andExpect(status().isNotFound());

        verify(productService).updateProductByCode(eq("INVALID"), any(UpdateProductDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProductByCode_WithInvalidDto_ShouldReturnBadRequest() throws Exception {
        // Given
        UpdateProductDto invalidDto = new UpdateProductDto(
                "", // blank name
                new BigDecimal("-50.00"), // negative price
                null // null availability
        );

        // When & Then
        mockMvc.perform(put("/api/products/TEST123456")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateProductByCode(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductByCode_WithValidCode_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProductByCode("TEST123456");

        // When & Then
        mockMvc.perform(delete("/api/products/TEST123456")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService).deleteProductByCode("TEST123456");
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deleteProductByCode_WithCustomerRole_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/products/TEST123456")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(productService, never()).deleteProductByCode(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductByCode_WithInvalidCode_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new ProductNotFoundException("INVALID")).when(productService).deleteProductByCode("INVALID");

        // When & Then
        mockMvc.perform(delete("/api/products/INVALID")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(productService).deleteProductByCode("INVALID");
    }

    @Test
    void deleteProductByCode_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/products/TEST123456")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(productService, never()).deleteProductByCode(any());
    }
}