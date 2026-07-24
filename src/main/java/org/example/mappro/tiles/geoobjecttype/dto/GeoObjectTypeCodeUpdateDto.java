package org.example.mappro.tiles.geoobjecttype.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeoObjectTypeCodeUpdateDto {
    @Size(max = 50, message = "Код не должен превышать 50 символов")
    private String code;
}