package org.example.mappro.tiles.importer;

import org.example.mappro.tiles.importer.model.ImportedPlacemark;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.io.WKTReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KmzObjectTypeResolverTest {
    private final WKTReader reader = new WKTReader();
    private final KmzObjectTypeResolver resolver = new KmzObjectTypeResolver();

    @Test
    void kuInNameHasPriorityAndResolvesToKy() throws Exception {
        var placemark = new ImportedPlacemark("подстанция ку-1", reader.read("POINT (1 1)"));

        assertEquals("KY", resolver.resolve(placemark).orElseThrow());
    }

    @Test
    void lineResolvesToLch() throws Exception {
        var placemark = new ImportedPlacemark("Линия 1", reader.read("LINESTRING (0 0, 1 1)"));

        assertEquals("LCH", resolver.resolve(placemark).orElseThrow());
    }

    @Test
    void unsupportedPolygonIsSkipped() throws Exception {
        var placemark = new ImportedPlacemark("Зона", reader.read("POLYGON ((0 0, 1 0, 1 1, 0 0))"));

        assertTrue(resolver.resolve(placemark).isEmpty());
    }
}
