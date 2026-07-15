package org.example.mappro.tiles.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ViolationCreateDto {
    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Severity is required")
    private String severity;

    private LocalDateTime date = LocalDateTime.now();

    private String description;
}
