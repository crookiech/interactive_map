package org.example.mappro.tiles.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ViolationCreateDto {

    @NotNull(message = "geoObjectId is required")
    private Long geoObjectId;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Severity is required")
    private String severity;   // HIGH, MEDIUM, LOW

    private LocalDateTime date = LocalDateTime.now();

    private String description;
}
