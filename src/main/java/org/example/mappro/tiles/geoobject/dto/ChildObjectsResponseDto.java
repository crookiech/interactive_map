package org.example.mappro.tiles.geoobject.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ChildObjectsResponseDto {
    private Long parentId;
    private String parentName;
    private List<Long> childIds;
}