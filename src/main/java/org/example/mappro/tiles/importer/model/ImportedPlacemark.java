package org.example.mappro.tiles.importer.model;

import org.locationtech.jts.geom.Geometry;

public record ImportedPlacemark(String name, Geometry geometry) {
}
