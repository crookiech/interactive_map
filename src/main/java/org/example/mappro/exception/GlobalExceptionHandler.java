package org.example.mappro.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.importer.model.KmzImportException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@Slf4j
@RestControllerAdvice(basePackages = "org.example.mappro")
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        log.info("Resource not found: {}", exception.getMessage());
        return error(HttpStatus.NOT_FOUND, exception.getCode(), exception.getMessage(), request);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ResourceConflictException exception,
            HttpServletRequest request
    ) {
        log.info("Resource conflict: {}", exception.getMessage());
        return error(HttpStatus.CONFLICT, exception.getCode(), exception.getMessage(), request);
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            RequestValidationException exception,
            HttpServletRequest request
    ) {
        log.info("Request validation failed: {}", exception.getMessage());
        return error(HttpStatus.BAD_REQUEST, exception.getCode(), exception.getMessage(), request);
    }

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            Exception exception,
            HttpServletRequest request
    ) {
        log.warn("Authentication failed: {}", exception.getMessage());
        return error(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_FAILED",
                "Неверные данные авторизации",
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        log.warn("Access denied: {}", exception.getMessage());
        return error(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "Недостаточно прав", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        log.info("Invalid request argument: {}", exception.getMessage());
        return error(
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                safeMessage(exception, "Некорректный запрос"),
                request
        );
    }

    @ExceptionHandler(KmzImportException.class)
    public ResponseEntity<ApiErrorResponse> handleKmzImport(
            KmzImportException exception,
            HttpServletRequest request
    ) {
        log.info("Invalid KMZ import: {}", exception.getMessage());
        return error(
                HttpStatus.BAD_REQUEST,
                "INVALID_KMZ_FILE",
                safeMessage(exception, "Некорректный KMZ-файл"),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ApiErrorResponse.FieldValidationError> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiErrorResponse.FieldValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        ApiErrorResponse body = new ApiErrorResponse(
                java.time.Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_FAILED",
                "Запрос содержит некорректные поля",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({
            MissingRequestValueException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            Exception exception,
            HttpServletRequest request
    ) {
        log.info("Cannot read request: {}", exception.getMessage());
        return error(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST",
                "Некорректное тело или параметры запроса",
                request
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleFileTooLarge(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request
    ) {
        log.info("Uploaded file is too large: {}", exception.getMessage());
        return error(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "FILE_TOO_LARGE",
                "Размер загружаемого файла превышает допустимый",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataConflict(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.warn("Database constraint conflict", exception);
        return error(
                HttpStatus.CONFLICT,
                "DATA_CONFLICT",
                "Данные конфликтуют с существующей записью",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unexpected server error", exception);
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Внутренняя ошибка сервера",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> error(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(status).body(
                ApiErrorResponse.of(
                        status.value(),
                        status.getReasonPhrase(),
                        code,
                        message,
                        request.getRequestURI()
                )
        );
    }

    private String safeMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }
}
