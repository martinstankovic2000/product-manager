package com.ingemark.productmanager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameInUseException extends RuntimeException{

    private final Object[] messageArgs;

    public UsernameInUseException(String param) {
        super("username.in.use");
        this.messageArgs = new Object[]{param};
    }
}
