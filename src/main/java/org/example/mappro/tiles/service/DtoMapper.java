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
                .type(geoObject.getType())
                .geometryWkt(geoObject.getGeometry() != null ? geoObject.getGeometry().toText() : null)
                .parentId(geoObject.getParentId())
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
                .geoObjectId(violation.getGeoObjectId())
                .type(violation.getType())
                .severity(violation.getSeverity())
                .date(violation.getDate())
                .description(violation.getDescription())
                .build();
    }
}