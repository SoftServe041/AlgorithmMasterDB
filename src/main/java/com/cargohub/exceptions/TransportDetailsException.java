package com.cargohub.exceptions;

public class TransportDetailsException extends RuntimeException {
    public TransportDetailsException(String message) {
        super(message);
    }

    public TransportDetailsException(Throwable cause) {
        super(cause);
    }
}
