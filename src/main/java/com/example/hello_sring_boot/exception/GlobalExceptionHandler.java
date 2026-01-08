package com.example.hello_sring_boot.exception;

import com.example.hello_sring_boot.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // Handle custom BaseException
        @ExceptionHandler(BaseException.class)
        public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
                log.error("BaseException: {}", ex.getMessage(), ex);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(ex.getStatus().value())
                                .error(ex.getStatus().getReasonPhrase())
                                .errorCode(ex.getErrorCode())
                                .message(ex.getMessage())
                                .path(request.getServletPath())
                                .build();

                return new ResponseEntity<>(errorResponse, ex.getStatus());
        }

        // Handle validation errors
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                MethodArgumentNotValidException ex, HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach((error) -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                log.error("Validation error: {}", errors);

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                        .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                        .errorCode("VALIDATION_FAILED")
                        .message("Validation failed")
                        .errors(errors)
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // Handle bad credentials (login failed)
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(
                BadCredentialsException ex, HttpServletRequest request) {

                log.error("BadCredentialsException: {}", ex.getStackTrace());
                log.error("BadCredentialsException: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .errorCode("INVALID_CREDENTIALS")
                        .message("auth.user.invalid_credentials")
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle access denied (403)
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                AccessDeniedException ex, HttpServletRequest request) {

                log.error("AccessDeniedException: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .errorCode("ACCESS_DENIED")
                        .message("You don't have permission to access this resource")
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        // Handle missing request parameters
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ErrorResponse> handleMissingParams(
                        MissingServletRequestParameterException ex, HttpServletRequest request) {

                String error = ex.getParameterName() + " parameter is missing";
                log.error("MissingServletRequestParameterException: {}", error);

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .errorCode("MISSING_PARAMETER")
                        .message(error)
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle invalid JSON format
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException ex, HttpServletRequest request) {

                log.error("HttpMessageNotReadableException: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .errorCode("INVALID_JSON")
                        .message("Invalid JSON format in request body")
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle type mismatch in parameters
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleTypeMismatch(
                MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

                String error = String.format("Parameter '%s' should be of type '%s'",
                        ex.getName(),
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

                log.error("MethodArgumentTypeMismatchException: {}", error);

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("type.mismatch")
                        .errorCode("TYPE_MISMATCH")
                        .message(error)
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle 404 - Not Found
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
                        NoHandlerFoundException ex, HttpServletRequest request) {

                log.error("NoHandlerFoundException: {} {}", ex.getHttpMethod(), ex.getRequestURL());

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .errorCode("ENDPOINT_NOT_FOUND")
                        .message("The requested endpoint was not found")
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Handle rate limit exception
        @ExceptionHandler(RateLimitException.class)
        public ResponseEntity<ErrorResponse> handleRateLimitException(RateLimitException ex,
                        HttpServletRequest request) {
                log.error("RateLimitException: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.TOO_MANY_REQUESTS.value())
                        .error(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
                        .errorCode("RATE_LIMIT_EXCEEDED")
                        .message("Rate limit exceeded")
                        .path(request.getServletPath())
                        .build();

                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(ex.getRetryAfterSeconds()))
                                .body(errorResponse);
        }

        // Handle EntityNotFoundException - 404
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .errorCode("ENDPOINT_NOT_FOUND")
                        .message(ex.getMessage())
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Handle all other exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
                log.error("Unhandled exception: ", ex);

                ErrorResponse errorResponse = ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .errorCode("INTERNAL_ERROR")
                        .message("An unexpected error occurred")
                        .path(request.getServletPath())
                        .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
