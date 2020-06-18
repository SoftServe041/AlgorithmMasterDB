package com.cargohub.exceptions;

public class RelationException extends RuntimeException {
    public RelationException(String message) {
        super(message);
    }

    public RelationException(Throwable cause) {
        super(cause);
    }
}
