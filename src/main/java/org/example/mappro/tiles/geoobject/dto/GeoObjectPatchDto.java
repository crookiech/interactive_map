package org.example.mappro.tiles.geoobject.dto;

import lombok.Data;

@Data
public class GeoObjectPatchDto {
    private String name;
    private Long parentId;
    private Long typeId;
    private String geometryWkt;
    private Integer labelPriority;
    private Boolean isSegment;
    private Integer segmentOrder;
}