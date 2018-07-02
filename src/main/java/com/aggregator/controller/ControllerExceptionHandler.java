package com.aggregator.controller;

import com.aggregator.exception.BankNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler
        extends ResponseEntityExceptionHandler {
    private static final Logger log =
            LogManager.getLogger(ControllerExceptionHandler.class);

        @ExceptionHandler({ BankNotFoundException.class })
        public ResponseEntity<String> handleBankNotFoundException(
                Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(
                    e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
}
