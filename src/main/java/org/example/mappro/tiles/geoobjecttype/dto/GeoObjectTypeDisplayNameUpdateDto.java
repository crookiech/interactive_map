package org.example.mappro.tiles.geoobjecttype.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeoObjectTypeDisplayNameUpdateDto {
    @Size(max = 255, message = "Отображаемое имя не должно превышать 255 символов")
    private String displayName;
}