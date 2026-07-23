package org.example.mappro.tiles.geoobjecttype.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeoObjectTypeLodUpdateDto {
    @NotNull(message = "lodMin is required")
    @Min(value = 0, message = "lodMin must be at least 0")
    @Max(value = 22, message = "lodMin must not exceed 22")
    private Integer lodMin;

    @NotNull(message = "lodMax is required")
    @Min(value = 0, message = "lodMax must be at least 0")
    @Max(value = 22, message = "lodMax must not exceed 22")
    private Integer lodMax;
}