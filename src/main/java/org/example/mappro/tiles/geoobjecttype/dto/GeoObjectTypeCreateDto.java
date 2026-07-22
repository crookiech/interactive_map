package org.example.mappro.tiles.geoobjecttype.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeoObjectTypeCreateDto {
    @NotBlank(message = "Type is required")
    private String name;
    
    /////////////////////////////////////////////////////
    private Integer lodMin;
    private Integer lodMax;
}
