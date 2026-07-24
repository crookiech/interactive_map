package org.example.mappro.tiles.geoobjecttype.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.mappro.tiles.geoobjecttype.GeometryTypeEnum;

@Data
public class GeoObjectTypePatchDto {
    @Size(max = 50, message = "Код не должен превышать 50 символов")
    private String code;

    @Size(max = 255, message = "Отображаемое имя не должно превышать 255 символов")
    private String displayName;

    private GeometryTypeEnum geometryType;

    @Min(value = 0, message = "lodMin must be at least 0")
    @Max(value = 22, message = "lodMin must not exceed 22")
    private Integer lodMin;

    @Min(value = 0, message = "lodMax must be at least 0")
    @Max(value = 22, message = "lodMax must not exceed 22")
    private Integer lodMax;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Цвет должен быть в формате HEX")
    private String colorHex;

    @Size(max = 100, message = "Ключ иконки не должен превышать 100 символов")
    private String iconKey;

    private Integer sortOrder;
    private Boolean visibleByDefault;
}
