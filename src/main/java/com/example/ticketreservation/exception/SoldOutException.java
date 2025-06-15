package com.example.ticketreservation.exception;

public class SoldOutException extends Exception {
    
    public SoldOutException() {
        super("チケットが売り切れています");
    }
    
    public SoldOutException(String message) {
        super(message);
    }
    
    public SoldOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
