// src/main/java/com/fitfatlab/fitfatlab_backend/common/exception/GlobalExceptionHandler.java
package com.fitfatlab.fitfatlab_backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Validación de @Valid / @Validated ────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        List<ApiError.FieldValidationError> fieldErrors = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .map(error -> {
                FieldError fieldError = (FieldError) error;
                return ApiError.FieldValidationError.builder()
                    .field(fieldError.getField())
                    .rejectedValue(fieldError.getRejectedValue())
                    .message(fieldError.getDefaultMessage())
                    .build();
            })
            .toList();

        ApiError apiError = ApiError.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation failed")
            .message("One or more fields are invalid")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .fieldErrors(fieldErrors)
            .build();

        log.warn("Validation error on {}: {}", request.getRequestURI(), fieldErrors);
        return ResponseEntity.badRequest().body(apiError);
    }

    // ── ResponseStatusException (404, 409, 403 lanzados desde Services) ─────

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(
        ResponseStatusException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
            .status(ex.getStatusCode().value())
            .error(HttpStatus.resolve(ex.getStatusCode().value()) != null
                ? HttpStatus.resolve(ex.getStatusCode().value()).getReasonPhrase()
                : "Error")
            .message(ex.getReason())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        log.warn("ResponseStatusException on {}: {} - {}",
            request.getRequestURI(), ex.getStatusCode(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(apiError);
    }

    // ── Seguridad — credenciales inválidas ───────────────────────────────────

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
        BadCredentialsException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Unauthorized")
            .message("Invalid email or password")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        log.warn("Bad credentials attempt on {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    // ── Seguridad — usuario deshabilitado ────────────────────────────────────

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledUser(
        DisabledException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("Forbidden")
            .message("User account is disabled")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    // ── Seguridad — sin permisos suficientes (@PreAuthorize) ─────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
        AccessDeniedException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("Forbidden")
            .message("You do not have permission to access this resource")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        log.warn("Access denied on {} ", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    // ── Seguridad — token JWT inválido o ausente ─────────────────────────────

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(
        AuthenticationException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Unauthorized")
            .message("Authentication required")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    // ── Parámetro de query faltante ──────────────────────────────────────────

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(
        MissingServletRequestParameterException ex,
        HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message("Required parameter '" + ex.getParameterName() + "' is missing")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.badRequest().body(apiError);
    }

    // ── Tipo de parámetro incorrecto (ej: UUID malformado en @PathVariable) ──

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex,
        HttpServletRequest request
    ) {
        String message = String.format(
            "Parameter '%s' must be of type %s",
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        ApiError apiError = ApiError.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message(message)
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.badRequest().body(apiError);
    }

    // ── Catch-all — cualquier excepción no manejada ──────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
        Exception ex,
        HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

        // Logueamos el stack completo solo en el catch-all
        log.error("Unhandled exception on {}: ", request.getRequestURI(), ex);
        return ResponseEntity.internalServerError().body(apiError);
    }
}