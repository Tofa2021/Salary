package com.example.backend.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private final Date date;
    private final String type;
    private final String info;
    private final String description;
    private Map<String, String> fields;

    public static ErrorResponse of(Throwable e, String description) {
        return ErrorResponse.builder()
                .date(new Date())
                .type(e.getClass().getSimpleName())
                .info(e.getMessage())
                .description(description)
                .build();
    }
}