package org.example.mappro.tiles.geoobjecttype.dto;

import org.example.mappro.tiles.geoobjecttype.GeometryTypeEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GeoObjectTypeCreateDto {
    private String code;
    private String displayName;
    private GeometryTypeEnum geometryType;
    private String colorHex;
    private String iconKey;
    private Integer sortOrder;
    private Boolean visibleByDefault;

    @Min(value = 0, message = "lodMin must be at least 0")
    @Max(value = 22, message = "lodMin must not exceed 22")
    private Integer lodMin;

    @Min(value = 0, message = "lodMax must be at least 0")
    @Max(value = 22, message = "lodMax must not exceed 22")
    private Integer lodMax;
}