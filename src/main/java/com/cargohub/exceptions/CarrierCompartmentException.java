package com.cargohub.exceptions;

public class CarrierCompartmentException extends RuntimeException {
    public CarrierCompartmentException(String message) {
        super(message);
    }

    public CarrierCompartmentException(Throwable cause) {
        super(cause);
    }
}
