package com.ingemark.productmanager.model.product.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HnbRateResponse (
        @JsonProperty("valuta")
        String valuta,
        @JsonProperty("srednji_tecaj")
        String srednjiTecaj
){
}
