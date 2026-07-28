package org.example.mappro.tiles.geoobjectviolation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoObjectViolationResponseDto {
    private Long id;
    private Long objectId;
    private String objectName;
    private String objectType;
    private Long violationId;
    private String violationTypeCode;
    private String violationTypeName;
    private String violationSeverity;
    private String violationDescription;
    private String status;
}
