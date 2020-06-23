package com.cargohub.neo4jGraph.ErrorHandler;

public class HubNotFoundException extends RuntimeException {

    public HubNotFoundException() {
    }

    public HubNotFoundException(String message) {
        super(message);
    }

    public HubNotFoundException(Throwable cause) {
        super(cause);
    }

    public HubNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HubNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
