package com.cargohub.exceptions;

public class CargoException extends RuntimeException {
    public CargoException(String message) {
        super(message);
    }

    public CargoException(Throwable cause) {
        super(cause);
    }
}
