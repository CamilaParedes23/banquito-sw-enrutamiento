package com.banquito.switchpagos.routing.exception;

public class InvalidRoutingEventException extends RuntimeException {

    private final String rejectionCode;

    public InvalidRoutingEventException(String rejectionCode, String message) {
        super(message);
        this.rejectionCode = rejectionCode;
    }

    public String getRejectionCode() {
        return rejectionCode;
    }
}
