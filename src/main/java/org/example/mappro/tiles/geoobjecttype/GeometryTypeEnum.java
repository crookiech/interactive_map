package org.example.mappro.tiles.geoobjecttype;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum GeometryTypeEnum {
    POINT,
    LINESTRING,
    LINEARRING,
    POLYGON,
    MULTIPOINT,
    MULTIPOLYGON,
    MULTILINESTRING;

    public static boolean isValid(String value) {
        if (value == null) return false;
        for (GeometryTypeEnum type : GeometryTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
    
    public static String getAllowedValues() {
        return Arrays.stream(GeometryTypeEnum.values())
            .map(Enum::name)
            .collect(Collectors.joining(", "));
    }
}