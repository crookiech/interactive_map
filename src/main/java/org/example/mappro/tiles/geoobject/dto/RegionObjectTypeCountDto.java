package org.example.mappro.tiles.geoobject.dto;

public record RegionObjectTypeCountDto(
        Long typeId,
        String typeCode,
        String typeName,
        long objectCount
) {
}
