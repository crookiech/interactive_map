package org.example.mappro.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.mappro.response.CustomResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "org.example.mappro.auth")
public class AuthExceptionHandler {

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<CustomResponse<Void>> handleAuthentication(Exception exception) {
        log.warn("Authentication failed: {}", exception.getMessage());
        return error(HttpStatus.UNAUTHORIZED, "Неверные данные авторизации");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        log.warn("Invalid auth request: {}", exception.getMessage());
        return error(HttpStatus.BAD_REQUEST, safeMessage(exception, "Некорректный запрос"));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            MissingRequestValueException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<CustomResponse<Void>> handleBadRequest(Exception exception) {
        log.warn("Cannot read auth request: {}", exception.getMessage());
        return error(HttpStatus.BAD_REQUEST, "Некорректное тело запроса");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponse<Void>> handleConflict(DataIntegrityViolationException exception) {
        log.warn("Auth data conflict", exception);
        return error(HttpStatus.CONFLICT, "Пользователь или роль с такими данными уже существует");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Void>> handleUnexpected(Exception exception) {
        log.error("Unexpected auth error", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }

    private ResponseEntity<CustomResponse<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new CustomResponse<>(status.value(), message, null));
    }

    private String safeMessage(IllegalArgumentException exception, String fallback) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }
}
