package com.aggregator.controller;

import com.aggregator.exception.BankNotFoundException;
import com.aggregator.exception.OperationNotSupportedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private static ObjectMapper mapper = new ObjectMapper();
    private static final Logger log =
            LogManager.getLogger(ControllerExceptionHandler.class);

        @ExceptionHandler({ BankNotFoundException.class,
                OperationNotSupportedException.class })
        public ResponseEntity<ObjectNode> handleUnprocessableEntity(
                Exception e) {
            log.error(e.getMessage(), e);

            ObjectNode message = mapper.createObjectNode();
            message.put("message", e.getMessage());
            return new ResponseEntity<>(
                    message, HttpStatus.UNPROCESSABLE_ENTITY);
        }
}
