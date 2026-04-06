package com.fitfatlab.fitfatlab_backend.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ApiError {

    private int status;
    private String error;
    private String message;
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // Solo se incluye cuando hay errores de validación (@Valid)
    private List<FieldValidationError> fieldErrors;

    @Getter
    @Builder
    public static class FieldValidationError {
        private String field;
        private Object rejectedValue;
        private String message;
    }
}