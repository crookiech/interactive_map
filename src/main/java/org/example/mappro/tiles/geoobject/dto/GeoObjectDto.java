package org.example.mappro.tiles.geoobject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoObjectDto {
    private Long id;
    private String name;
    private String type;
    private String geometryJson;
    private Long parentId;
    private Integer labelPriority;
    private Boolean isSegment;
    private Integer segmentOrder;
    private Long violationCount;
    private String[] violationTypes;
}
