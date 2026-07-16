package org.example.mappro.tiles.importer.service;

import org.example.mappro.tiles.importer.model.KmzImportException;
import org.example.mappro.tiles.importer.model.ImportedPlacemark;
import org.locationtech.jts.geom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class KmlGeometryParser {
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<ImportedPlacemark> parse(String kml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(kml)));
            NodeList nodes = document.getElementsByTagNameNS("*", "Placemark");
            List<ImportedPlacemark> result = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element placemark = (Element) nodes.item(i);
                String name = directChildText(placemark, "name");
                Geometry geometry = firstGeometry(placemark);
                if (geometry != null && !geometry.isEmpty()) {
                    geometry.setSRID(4326);
                    result.add(new ImportedPlacemark(
                            name == null || name.isBlank() ? "KMZ object " + (i + 1) : name.trim(),
                            geometry));
                }
            }
            return result;
        } catch (Exception e) {
            throw new KmzImportException("Cannot parse KML: " + e.getMessage(), e);
        }
    }

    private Geometry firstGeometry(Element parent) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof Element element)) continue;
            Geometry geometry = parseGeometry(element);
            if (geometry != null) return geometry;
        }
        return null;
    }

    private Geometry parseGeometry(Element element) {
        return switch (element.getLocalName()) {
            case "Point" -> parsePoint(element);
            case "LineString" -> geometryFactory.createLineString(coordinates(element));
            case "Polygon" -> parsePolygon(element);
            case "MultiGeometry" -> parseMultiGeometry(element);
            default -> null;
        };
    }

    private Point parsePoint(Element element) {
        Coordinate[] coordinates = coordinates(element);
        if (coordinates.length != 1) {
            throw new KmzImportException("Point must contain exactly one coordinate");
        }
        return geometryFactory.createPoint(coordinates[0]);
    }

    private Polygon parsePolygon(Element polygon) {
        Element outer = firstDescendant(polygon, "outerBoundaryIs");
        if (outer == null) throw new KmzImportException("Polygon has no outer boundary");
        LinearRing shell = ring(outer);

        NodeList innerNodes = polygon.getElementsByTagNameNS("*", "innerBoundaryIs");
        LinearRing[] holes = new LinearRing[innerNodes.getLength()];
        for (int i = 0; i < innerNodes.getLength(); i++) {
            holes[i] = ring((Element) innerNodes.item(i));
        }
        return geometryFactory.createPolygon(shell, holes);
    }

    private Geometry parseMultiGeometry(Element multiGeometry) {
        List<Geometry> geometries = new ArrayList<>();
        for (Node child = multiGeometry.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element) {
                Geometry geometry = parseGeometry(element);
                if (geometry != null) geometries.add(geometry);
            }
        }
        return geometryFactory.createGeometryCollection(geometries.toArray(Geometry[]::new));
    }

    private LinearRing ring(Element boundary) {
        Element linearRing = firstDescendant(boundary, "LinearRing");
        if (linearRing == null) throw new KmzImportException("Polygon boundary has no LinearRing");
        Coordinate[] coordinates = coordinates(linearRing);
        if (coordinates.length > 0 && !coordinates[0].equals2D(coordinates[coordinates.length - 1])) {
            Coordinate[] closed = new Coordinate[coordinates.length + 1];
            System.arraycopy(coordinates, 0, closed, 0, coordinates.length);
            closed[closed.length - 1] = coordinates[0].copy();
            coordinates = closed;
        }
        return geometryFactory.createLinearRing(coordinates);
    }

    private Coordinate[] coordinates(Element geometry) {
        Element node = firstDescendant(geometry, "coordinates");
        if (node == null || node.getTextContent().isBlank()) return new Coordinate[0];
        String[] tuples = node.getTextContent().trim().split("\\s+");
        Coordinate[] result = new Coordinate[tuples.length];
        for (int i = 0; i < tuples.length; i++) {
            String[] values = tuples[i].split(",");
            if (values.length < 2) throw new KmzImportException("Invalid KML coordinate: " + tuples[i]);
            double x = Double.parseDouble(values[0]);
            double y = Double.parseDouble(values[1]);
            result[i] = values.length >= 3 && !values[2].isBlank()
                    ? new Coordinate(x, y, Double.parseDouble(values[2]))
                    : new Coordinate(x, y);
        }
        return result;
    }

    private Element firstDescendant(Element parent, String localName) {
        NodeList nodes = parent.getElementsByTagNameNS("*", localName);
        return nodes.getLength() == 0 ? null : (Element) nodes.item(0);
    }

    private String directChildText(Element parent, String localName) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element && localName.equals(element.getLocalName())) {
                return element.getTextContent();
            }
        }
        return null;
    }
}
