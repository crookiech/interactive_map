package org.example.mappro.tiles.violationtype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ViolationTypeCreateDto {
    @NotBlank(message = "Code is required")
    @Size(max = 100, message = "Code must not exceed 100 characters")
    @Pattern(
        regexp = "^[A-Za-z][A-Za-z0-9_]*$",
        message = "Code must start with a letter and contain only Latin letters, digits, and underscores"
    )
    private String code;

    @NotBlank(message = "Display name is required")
    @Size(max = 255, message = "Display name must not exceed 255 characters")
    private String displayName;
}
