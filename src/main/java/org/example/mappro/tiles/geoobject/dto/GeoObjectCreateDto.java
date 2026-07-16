package org.example.mappro.tiles.geoobject.dto;

import lombok.Data;

@Data
public class GeoObjectCreateDto {
    private String name;
    private String type;
    private String geometryWkt;
    private Long parentId;
    private Integer lodMin;
    private Integer lodMax;
    private Integer labelPriority;
    private Boolean isSegment;
    private Integer segmentOrder;
}
