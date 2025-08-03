package com.ingemark.productmanager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    private final Object[] messageArgs;

    public UserNotFoundException(String username) {
        super("user.not.found");
        this.messageArgs = new Object[]{username};
    }
}
