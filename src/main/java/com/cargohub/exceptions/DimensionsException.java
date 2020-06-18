package com.cargohub.exceptions;

public class DimensionsException extends RuntimeException {
    public DimensionsException(String message) {
        super(message);
    }

    public DimensionsException(Throwable cause) {
        super(cause);
    }
}
