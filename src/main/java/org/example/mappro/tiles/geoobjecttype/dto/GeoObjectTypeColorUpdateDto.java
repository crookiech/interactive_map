package org.example.mappro.tiles.geoobjecttype.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GeoObjectTypeColorUpdateDto {
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Цвет должен быть в формате HEX (например, #FF0000)")
    private String colorHex;
}