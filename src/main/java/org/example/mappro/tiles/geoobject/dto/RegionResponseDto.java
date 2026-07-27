package org.example.mappro.tiles.geoobject.dto;

import java.util.List;

public record RegionResponseDto(
        Long id,
        String name,
        List<RegionObjectTypeCountDto> objectTypes
) {
}
