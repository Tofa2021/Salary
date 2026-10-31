package com.example.backend.exception;

import com.example.backend.exception.notFound.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFoundException(NotFoundException e, WebRequest request) {
        return newException(e, request);
    }

    private Object newException(RuntimeException exception, WebRequest request) {
        final String message = exception.getMessage();
        if (message == null) {
            return new ExceptionDTO(exception.getClass().getName(), request.getDescription(false));
        }

        return new InfoExceptionDTO(exception.getClass().getName(), message, request.getDescription(false));
    }

    @AllArgsConstructor
    @Data
    private static class ExceptionDTO {
        final Date date = new Date();
        String type;
        String description;
    }

    @AllArgsConstructor
    @Builder
    @Data
    private static class InfoExceptionDTO {
        final Date date = new Date();
        String type;
        String info;
        String description;
    }
}
