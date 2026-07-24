package org.example.mappro.tiles.geoobject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Создание объекта")
public class GeoObjectCreateDto {
    private String name;
    private String type;
    private String geometryWkt;
    private Long parentId;
    private Integer labelPriority;
    private Boolean isSegment;
    private Integer segmentOrder;
}
