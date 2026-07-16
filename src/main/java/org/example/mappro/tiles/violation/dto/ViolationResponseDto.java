package org.example.mappro.tiles.violation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationResponseDto {
    private Long id;
    private String type;
    private String severity;
    private LocalDateTime date;
    private String description;
    private String status;
}
