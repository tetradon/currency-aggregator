package com.aggregator.exception;

public class OperationNotSupportedException extends RuntimeException {
    public OperationNotSupportedException() {
        super();
    }

    public OperationNotSupportedException(String message) {
        super("operation '" + message + "' not supported");
    }

    public OperationNotSupportedException(String message, Throwable cause) {
        super("operation '" + message + "' not supported", cause);
    }

    public OperationNotSupportedException(Throwable cause) {
        super(cause);
    }
}
