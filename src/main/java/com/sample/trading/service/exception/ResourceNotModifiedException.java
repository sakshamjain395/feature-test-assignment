package com.sample.trading.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.ACCEPTED)
public class ResourceNotModifiedException extends RuntimeException  {
    public ResourceNotModifiedException() {
        super();
    }

    public ResourceNotModifiedException(String message) {
        super(message);
    }
}
