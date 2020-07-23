package com.cargohub.controllers;


import com.cargohub.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Date;

@ControllerAdvice
public class ControllerAdviceImpl {

    @ExceptionHandler(value = {HubException.class, HubNotFoundException.class})
    public ResponseEntity<Object> handleHubException(RuntimeException e) {
        ErrorMessage error = new ErrorMessage(new Date(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {OrderException.class, LogsClearException.class})
    public ResponseEntity<Object> handleOrderException(RuntimeException e) {
        ErrorMessage error = new ErrorMessage(new Date(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {CargoException.class, CargoPositionException.class, RelationException.class})
    public ResponseEntity<Object> handleCargoException(RuntimeException e) {
        ErrorMessage error = new ErrorMessage(new Date(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {CarrierCompartmentException.class, DimensionsException.class,
            TransportDetailsException.class, TransporterException.class})
    public ResponseEntity<Object> handleTransportException(RuntimeException e) {
        ErrorMessage error = new ErrorMessage(new Date(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleTransportException(Exception e) {
        ErrorMessage error = new ErrorMessage(new Date(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
