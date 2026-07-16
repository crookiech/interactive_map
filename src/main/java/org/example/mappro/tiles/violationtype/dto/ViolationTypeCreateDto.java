package org.example.mappro.tiles.violationtype.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ViolationTypeCreateDto {
    @NotBlank(message = "Type is required")
    private String name;
}
