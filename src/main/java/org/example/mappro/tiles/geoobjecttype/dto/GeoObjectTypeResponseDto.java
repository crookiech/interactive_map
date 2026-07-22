package org.example.mappro.tiles.geoobjecttype.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoObjectTypeResponseDto {
    private Long id;
    private String name;

    ///////////////////////////////////
    private Integer lodMin;
    private Integer lodMax;
    ///////////////////////////////////
}
