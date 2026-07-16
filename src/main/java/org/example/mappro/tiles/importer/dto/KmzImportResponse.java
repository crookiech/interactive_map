package org.example.mappro.tiles.importer.dto;

import java.util.List;

public record KmzImportResponse(
        int placemarksRead,
        int objectsCreated,
        int skipped,
        List<String> warnings
) {
}
