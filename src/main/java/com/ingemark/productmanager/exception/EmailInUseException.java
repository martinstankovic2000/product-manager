package com.ingemark.productmanager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailInUseException extends RuntimeException{

    private final Object[] messageArgs;

    public EmailInUseException(String param) {
        super("email.in.use");
        this.messageArgs = new Object[]{param};
    }
}
