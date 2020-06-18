package com.cargohub.exceptions;

public class TruckException extends RuntimeException {
    public TruckException(String message) {
        super(message);
    }

    public TruckException(Throwable cause) {
        super(cause);
    }
}
