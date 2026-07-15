package org.example.mappro.tiles.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeoObjectCreateDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Geometry WKT is required")
    private String geometryWkt;

    private Long parentId;
    private Integer lodMin = 0;
    private Integer lodMax = 22;
    private Integer labelPriority = 1;
    private Boolean isSegment = false;
    private Integer segmentOrder;
}