package com.aggregator.controller;

import com.aggregator.exception.BankNotFoundException;
import com.aggregator.exception.CurrencyNotFoundException;
import com.aggregator.exception.OperationNotSupportedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbc.JdbcSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler
        extends ResponseEntityExceptionHandler {
    private static ObjectMapper mapper = new ObjectMapper();
    private static final Logger log =
            LogManager.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({BankNotFoundException.class,
            OperationNotSupportedException.class,
            CurrencyNotFoundException.class
    })
    public ResponseEntity<ObjectNode> handleUnprocessableEntity(
            Exception e) {
        log.warn(e.getMessage(), e);

        ObjectNode message = mapper.createObjectNode();
        message.put("message", e.getMessage());
        return new ResponseEntity<>(
                message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ObjectNode> handleNumberFormatException(
            NumberFormatException e) {
        log.warn(e.getMessage(), e);
        ObjectNode message = mapper.createObjectNode();
        message.put("message", e.getMessage());
        return new ResponseEntity<>(
                message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JdbcSQLException.class)
    public ResponseEntity<ObjectNode> handleException(
            Exception e) {
        log.warn(e.getMessage(), e);
        ObjectNode message = mapper.createObjectNode();
        message.put("message", e.getMessage());
        return new ResponseEntity<>(
                message, HttpStatus.CONFLICT);
    }
}
