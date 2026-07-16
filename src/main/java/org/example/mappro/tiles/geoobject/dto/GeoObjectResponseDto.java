package org.example.mappro.tiles.geoobject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoObjectResponseDto {
    private Long id;
    private String name;
    private String typeName;
    private Long typeId;
    private String geometryWkt;
    private Long parentId;
    private String parentName;
    private Integer lodMin;
    private Integer lodMax;
    private Integer labelPriority;
    private Boolean isSegment;
    private Integer segmentOrder;
}
