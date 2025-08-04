package com.ingemark.productmanager.service;

import com.ingemark.productmanager.model.product.response.HnbRateResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service to handle currency conversion.
 * Fetches EUR to USD exchange rate from HNB API and calculates USD price.
 * Falls back to a predefined rate on API failure.
 */
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final RestTemplate restTemplate;
    private final MessageService messageService;

    @Value("${hnb.api.url}")
    private String hnbApiUrl;

    private static final BigDecimal FALLBACK_RATE = new BigDecimal("1.10");
    private static final String US_CURRENCY = "USD";
    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    /**
     * Calculates the USD price for a given EUR price using the latest exchange rate.
     * Falls back to a fixed rate if the external API is unavailable.
     *
     * @param priceEur Price in EUR.
     * @return Equivalent price in USD, rounded to 2 decimals.
     */
    public BigDecimal calculateUsdPrice(BigDecimal priceEur) {
        try {
            BigDecimal exchangeRate = getEurToCurrencyRate(US_CURRENCY);
            return priceEur.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            String errorMessage = messageService.getMessage("currency.service.unavailable");
            log.warn(errorMessage, e);
            return priceEur.multiply(FALLBACK_RATE).setScale(2, RoundingMode.HALF_UP);
        }
    }

    /**
     * Retrieves EUR to target currency exchange rate from HNB API.
     *
     * @param currency Target currency code (e.g., "USD").
     * @return Exchange rate as BigDecimal.
     * @throws IllegalStateException if no exchange rate data is returned.
     */
    private BigDecimal getEurToCurrencyRate(String currency) {
        ResponseEntity<HnbRateResponse[]> response = restTemplate.getForEntity(hnbApiUrl + currency, HnbRateResponse[].class);
        HnbRateResponse[] rates = response.getBody();

        if (rates == null || rates.length == 0) {
            throw new IllegalStateException("No exchange rate data returned from HNB");
        }

        String rateStr = rates[0].srednjiTecaj().replace(",", "."); // HNB uses commas as decimal separator
        return new BigDecimal(rateStr);
    }
}
