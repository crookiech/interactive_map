package org.example.mappro.exception;

public class RequestValidationException extends ApiException {

    public RequestValidationException(String code, String message) {
        super(code, message);
    }

    public RequestValidationException(String code, String message, Throwable cause) {
        super(code, message);
        initCause(cause);
    }
}
