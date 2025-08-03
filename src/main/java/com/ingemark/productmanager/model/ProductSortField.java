package com.ingemark.productmanager.model;


public enum ProductSortField {
    NAME("name"),
    PRICE("price");

    private final String field;

    ProductSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
