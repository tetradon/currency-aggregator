package com.aggregator.exception;

public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException() {
        super();
    }

    public BankNotFoundException(String message) {
        super("'" + message + "' does not exist");
    }

    public BankNotFoundException(String message, Throwable cause) {
        super("'" + message + "' does not exist", cause);
    }

    public BankNotFoundException(Throwable cause) {
        super(cause);
    }
}
