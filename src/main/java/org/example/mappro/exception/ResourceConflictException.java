package org.example.mappro.exception;

public class ResourceConflictException extends ApiException {

    public ResourceConflictException(String code, String message) {
        super(code, message);
    }
}
