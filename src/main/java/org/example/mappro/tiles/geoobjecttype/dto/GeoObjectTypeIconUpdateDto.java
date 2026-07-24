package org.example.mappro.tiles.geoobjecttype.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeoObjectTypeIconUpdateDto {
    @Size(max = 100, message = "Ключ иконки не должен превышать 100 символов")
    private String iconKey;
}