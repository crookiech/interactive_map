package org.example.mappro.tiles.violation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ViolationCreateDto {
    @NotBlank(message = "Type code is required")
    private String typeCode;

    @NotBlank(message = "Severity is required")
    private String severity;

    private LocalDateTime date = LocalDateTime.now();

    private String description;
}
