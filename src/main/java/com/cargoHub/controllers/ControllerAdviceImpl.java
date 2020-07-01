package com.cargohub.controllers;


import com.cargohub.neo4jGraph.ErrorHandler.ErrorResponce;
import com.cargohub.neo4jGraph.ErrorHandler.HubNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdviceImpl {

    @ExceptionHandler
    public ResponseEntity<ErrorResponce> handleException(HubNotFoundException e) {
        ErrorResponce error = new ErrorResponce(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
