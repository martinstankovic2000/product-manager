package com.ingemark.productmanager.model.product;


public enum ProductSortField {
    NAME("name"),
    PRICE("priceEur");

    private final String field;

    ProductSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
