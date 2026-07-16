package org.example.mappro.tiles.importer;

import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.importer.service.RegionGeometrySplitter;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.io.WKTReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RegionGeometrySplitterTest {
    private final WKTReader reader = new WKTReader();

    @Test
    void splitsLineBetweenTwoRegionsAndUncoveredArea() throws Exception {
        GeoObject first = region(1L, "POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0))");
        GeoObject second = region(2L, "POLYGON ((10 0, 20 0, 20 10, 10 10, 10 0))");

        var parts = new RegionGeometrySplitter().split(
                reader.read("LINESTRING (-5 5, 25 5)"), List.of(first, second));

        assertEquals(4, parts.size());
        assertNull(parts.get(0).region());
        assertEquals(1L, parts.get(1).region().getId());
        assertEquals(2L, parts.get(2).region().getId());
        assertNull(parts.get(3).region());
        assertEquals(30.0, parts.stream().mapToDouble(part -> part.geometry().getLength()).sum(), 0.00001);
    }

    private GeoObject region(long id, String wkt) throws Exception {
        GeoObject object = new GeoObject();
        object.setId(id);
        object.setGeometry(reader.read(wkt));
        return object;
    }
}
