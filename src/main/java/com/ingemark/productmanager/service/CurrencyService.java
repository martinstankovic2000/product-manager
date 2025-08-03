package com.ingemark.productmanager.service;

import com.ingemark.productmanager.model.response.HnbRateResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final RestTemplate restTemplate;

    @Value("${hnb.api.url}")
    private String hnbApiUrl;

    private static final BigDecimal FALLBACK_RATE = new BigDecimal("1.10");
    private static final String US_CURRENCY = "USD";
    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    public BigDecimal calculateUsdPrice(BigDecimal priceEur) {
        try {
            BigDecimal exchangeRate = getEurToCurrencyRate(US_CURRENCY);
            return priceEur.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.warn("Failed to get exchange rate from HNB API, using fallback rate", e);
            return priceEur.multiply(FALLBACK_RATE).setScale(2, RoundingMode.HALF_UP);
        }
    }

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
