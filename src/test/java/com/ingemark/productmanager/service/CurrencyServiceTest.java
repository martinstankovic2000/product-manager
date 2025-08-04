package com.ingemark.productmanager.service;

import com.ingemark.productmanager.model.product.response.HnbRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CurrencyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private CurrencyService currencyService;

    @Value("${hnb.api.url}")
    private String hnbApiUrl;


    @Test
    void calculateUsdPrice_WithValidApiResponse_ReturnsCalculatedPrice() {
        HnbRateResponse rateResponse = new HnbRateResponse("USD", "1,20");
        ResponseEntity<HnbRateResponse[]> responseEntity = new ResponseEntity<>(new HnbRateResponse[]{rateResponse}, HttpStatus.OK);

        when(restTemplate.getForEntity(hnbApiUrl + "USD", HnbRateResponse[].class)).thenReturn(responseEntity);

        BigDecimal priceEur = new BigDecimal("100");
        BigDecimal expectedUsdPrice = new BigDecimal("120.00");

        BigDecimal result = currencyService.calculateUsdPrice(priceEur);

        assertEquals(expectedUsdPrice, result);
        verify(restTemplate).getForEntity(hnbApiUrl + "USD", HnbRateResponse[].class);
    }

    @Test
    void calculateUsdPrice_WhenApiFails_UsesFallbackRate() {
        when(restTemplate.getForEntity(anyString(), eq(HnbRateResponse[].class))).thenThrow(new RuntimeException("API down"));

        BigDecimal priceEur = new BigDecimal("100");
        BigDecimal expectedUsdPrice = new BigDecimal("110.00");  // fallback 1.10 * 100

        BigDecimal result = currencyService.calculateUsdPrice(priceEur);

        assertEquals(expectedUsdPrice, result);
    }

    @Test
    void calculateUsdPrice_WhenNoRates_UsesFallbackRate() {
        ResponseEntity<HnbRateResponse[]> responseEntity = new ResponseEntity<>(new HnbRateResponse[0], HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(HnbRateResponse[].class))).thenReturn(responseEntity);

        BigDecimal priceEur = new BigDecimal("100");
        BigDecimal expectedUsdPrice = new BigDecimal("110.00"); // fallback 1.10 * 100

        BigDecimal result = currencyService.calculateUsdPrice(priceEur);

        assertEquals(expectedUsdPrice, result);
    }
}
