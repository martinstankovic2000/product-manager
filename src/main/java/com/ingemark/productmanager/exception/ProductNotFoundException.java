package com.ingemark.productmanager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    private final Object[] messageArgs;

    public ProductNotFoundException(String code) {
        super("product.not.found");
        this.messageArgs = new Object[]{code};
    }
}
