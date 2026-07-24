package org.example.mappro.tiles.geoobjecttype.dto;

import org.example.mappro.tiles.geoobjecttype.GeometryTypeEnum;
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
    private String code;
    private String displayName;
    private GeometryTypeEnum geometryType;
    private Integer lodMin;
    private Integer lodMax;
    private String colorHex;
    private String iconKey;
    private Integer sortOrder;
    private Boolean visibleByDefault;
}
