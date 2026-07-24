package org.example.mappro.exception;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String code, String message) {
        super(code, message);
    }
}
