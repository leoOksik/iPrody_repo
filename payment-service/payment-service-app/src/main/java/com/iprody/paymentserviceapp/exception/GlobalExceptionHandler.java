package com.iprody.paymentserviceapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto notFoundExceptionHandler(EntityNotFoundException ex) {
        return new ErrorDto(
            HttpStatus.NOT_FOUND.value(),
            ex.getLocalizedMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleOther(Exception ex) {
        return new ErrorDto(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getLocalizedMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ErrorDto(
            HttpStatus.BAD_REQUEST.value(),
            ex.getLocalizedMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleArgumentNotValid(MethodArgumentNotValidException ex) {
        return new ErrorDto(
            HttpStatus.BAD_REQUEST.value(),
            ex.getLocalizedMessage()
        );
    }
}
