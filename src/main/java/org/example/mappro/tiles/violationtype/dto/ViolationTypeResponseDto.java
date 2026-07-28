package org.example.mappro.tiles.violationtype.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationTypeResponseDto {
    private Long id;
    private String code;
    private String displayName;
}
