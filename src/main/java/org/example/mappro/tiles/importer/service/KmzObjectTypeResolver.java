package org.example.mappro.tiles.importer.service;

import org.example.mappro.tiles.importer.model.ImportedPlacemark;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class KmzObjectTypeResolver {
    static final String KU_TYPE = "KY";
    static final String LINEAR_TYPE = "LCH";

    public Optional<String> resolve(ImportedPlacemark placemark) {
        if (placemark.name().toUpperCase(Locale.ROOT).contains("КУ")) {
            return Optional.of(KU_TYPE);
        }
        if (placemark.geometry().getDimension() == 1) {
            return Optional.of(LINEAR_TYPE);
        }
        return Optional.empty();
    }
}
