package org.example.mappro.tiles.geoobjectviolation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeoObjectViolationCreateDto {
    @NotNull(message = "Object ID is required")
    private Long objectId;

    @NotNull(message = "Violation ID is required")
    private Long violationId;
}
