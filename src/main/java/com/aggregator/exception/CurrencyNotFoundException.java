package com.aggregator.exception;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException() {
        super();
    }

    public CurrencyNotFoundException(String code) {
        super("currency '" + code + "' not found");
    }

    public CurrencyNotFoundException(String code, Throwable cause) {
        super("currency '" + code + "' not found", cause);
    }

    public CurrencyNotFoundException(Throwable cause) {
        super(cause);
    }
}
