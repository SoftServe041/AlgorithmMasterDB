package com.cargohub.exceptions;

public class TransporterException extends RuntimeException {
    public TransporterException(String message) {
        super(message);
    }

    public TransporterException(Throwable cause) {
        super(cause);
    }
}
