package org.example.mappro.tiles.importer;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.LineString;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KmlGeometryParserTest {
    @Test
    void parsesPlacemarkLineString() {
        String kml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <kml xmlns="http://www.opengis.net/kml/2.2">
                  <Document><Placemark><name>Road</name><LineString>
                    <coordinates>0,5,0 20,5,0</coordinates>
                  </LineString></Placemark></Document>
                </kml>
                """;

        var result = new KmlGeometryParser().parse(kml);

        assertEquals(1, result.size());
        assertEquals("Road", result.getFirst().name());
        assertEquals(4326, result.getFirst().geometry().getSRID());
        assertEquals(LineString.class, result.getFirst().geometry().getClass());
    }
}
