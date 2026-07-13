package org.example.mappro.tiles.dto;

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
    private String type;
    private String geometryWkt;  // геометрия в виде WKT строки
    private Long parentId;
    private Integer lodMin;
    private Integer lodMax;
    private Integer labelPriority;
    private Boolean isSegment;
    private Integer segmentOrder;
}