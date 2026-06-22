package com.insurance.backend.exception;

import com.insurance.backend.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex){
        ErrorResponse response = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex){
        ErrorResponse response = ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
