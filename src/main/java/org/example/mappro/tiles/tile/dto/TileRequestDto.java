package org.example.mappro.tiles.tile.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TileRequestDto {
    private int z;
    private int x;
    private int y;
    private List<String> types;
    private String lang;
}
