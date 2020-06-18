package com.cargohub.exceptions;

public class HubException extends RuntimeException {
    public HubException(String message) {
        super(message);
    }

    public HubException(Throwable cause) {
        super(cause);
    }
}
