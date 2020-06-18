package com.cargohub.exceptions;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }

    public OrderException(Throwable cause) {
        super(cause);
    }
}
