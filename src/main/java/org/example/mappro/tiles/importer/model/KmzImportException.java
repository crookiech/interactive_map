package org.example.mappro.tiles.importer.model;

public class KmzImportException extends RuntimeException {
    public KmzImportException(String message) {
        super(message);
    }

    public KmzImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
