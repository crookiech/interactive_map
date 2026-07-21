package org.example.mappro.tiles.tileMVT.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TileRequestDto {
    private int z;
    private int x;
    private int y;
    private List<String> types;
    private List<String> severities;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;
    private Boolean showCities;
    private String lang;
}