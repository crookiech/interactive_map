package org.example.mappro.tiles.service;

import org.example.mappro.tiles.dto.GeoObjectResponseDto;
import org.example.mappro.tiles.dto.ViolationResponseDto;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.model.Violation;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public GeoObjectResponseDto toGeoObjectResponseDto(GeoObject geoObject) {
        if (geoObject == null) {
            return null;
        }

        return GeoObjectResponseDto.builder()
            .id(geoObject.getId())
            .name(geoObject.getName())
            .typeName(geoObject.getType() != null ? geoObject.getType().getName() : null)
            .typeId(geoObject.getType() != null ? geoObject.getType().getId() : null)
            .geometryWkt(geoObject.getGeometry() != null ? geoObject.getGeometry().toText() : null)
            .parentId(geoObject.getParent() != null ? geoObject.getParent().getId() : null)
            .parentName(geoObject.getParent() != null ? geoObject.getParent().getName() : null)
            .lodMin(geoObject.getLodMin())
            .lodMax(geoObject.getLodMax())
            .labelPriority(geoObject.getLabelPriority())
            .isSegment(geoObject.getIsSegment())
            .segmentOrder(geoObject.getSegmentOrder())
            .build();
    }

    public ViolationResponseDto toViolationResponseDto(Violation violation) {
        if (violation == null) {
            return null;
        }

        return ViolationResponseDto.builder()
            .id(violation.getId())
            .typeId(violation.getType() != null ? violation.getType().getId() : null)
            .severity(violation.getSeverity())
            .date(violation.getDate())
            .description(violation.getDescription())
            .build();
    }
}
