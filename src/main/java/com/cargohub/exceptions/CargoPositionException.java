package com.cargohub.exceptions;

public class CargoPositionException extends RuntimeException {
    public CargoPositionException(String message) {
        super(message);
    }

    public CargoPositionException(Throwable cause) {
        super(cause);
    }
}
