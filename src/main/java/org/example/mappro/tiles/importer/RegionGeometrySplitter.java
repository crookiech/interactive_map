package org.example.mappro.tiles.importer;

import org.example.mappro.tiles.model.GeoObject;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.geom.util.GeometryFixer;
import org.locationtech.jts.linearref.LengthIndexedLine;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegionGeometrySplitter {
    public List<GeometryPart> split(Geometry source, List<GeoObject> regions) {
        List<GeometryPart> parts = new ArrayList<>();
        Geometry remaining = source;

        for (GeoObject region : regions) {
            if (remaining.isEmpty() || region.getGeometry() == null || region.getGeometry().isEmpty()) continue;
            if (!remaining.getEnvelopeInternal().intersects(region.getGeometry().getEnvelopeInternal())) continue;

            Geometry inside = intersection(remaining, region.getGeometry());
            addComponents(parts, inside, region);
            if (!inside.isEmpty()) remaining = difference(remaining, region.getGeometry());
        }
        addComponents(parts, remaining, null);
        if (source.getDimension() == 1) {
            LengthIndexedLine indexedSource = new LengthIndexedLine(source);
            parts.sort((left, right) -> Double.compare(
                    position(indexedSource, left.geometry()),
                    position(indexedSource, right.geometry())));
        }
        return parts;
    }

    private double position(LengthIndexedLine source, Geometry part) {
        if (part.getCoordinates().length == 0) return Double.MAX_VALUE;
        double first = source.indexOf(part.getCoordinates()[0]);
        double last = source.indexOf(part.getCoordinates()[part.getCoordinates().length - 1]);
        return Math.min(first, last);
    }

    private Geometry intersection(Geometry left, Geometry right) {
        try {
            return left.intersection(right);
        } catch (TopologyException e) {
            return GeometryFixer.fix(left).intersection(GeometryFixer.fix(right));
        }
    }

    private Geometry difference(Geometry left, Geometry right) {
        try {
            return left.difference(right);
        } catch (TopologyException e) {
            return GeometryFixer.fix(left).difference(GeometryFixer.fix(right));
        }
    }

    private void addComponents(List<GeometryPart> target, Geometry geometry, GeoObject region) {
        if (geometry == null || geometry.isEmpty()) return;
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry component = geometry.getGeometryN(i).copy();
            if (!component.isEmpty()) {
                component.setSRID(4326);
                target.add(new GeometryPart(component, region));
            }
        }
    }

    public record GeometryPart(Geometry geometry, GeoObject region) {
    }
}
