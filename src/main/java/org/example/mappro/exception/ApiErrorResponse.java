package org.example.mappro.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldValidationError> fieldErrors
) {
    public static ApiErrorResponse of(
            int status,
            String error,
            String code,
            String message,
            String path
    ) {
        return new ApiErrorResponse(Instant.now(), status, error, code, message, path, List.of());
    }

    public record FieldValidationError(String field, String message) {
    }
}
