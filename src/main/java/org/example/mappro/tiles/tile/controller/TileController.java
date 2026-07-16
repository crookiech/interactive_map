package org.example.mappro.tiles.tile.controller;

import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.tile.dto.TileRequestDto;
import org.example.mappro.tiles.tile.service.TileService;
import org.geojson.FeatureCollection;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tiles")
@RequiredArgsConstructor
public class TileController {
    private final TileService tileService;

    @GetMapping("/{z}/{x}/{y}")
    public ResponseEntity<FeatureCollection> getTile(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<String> severities,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) Boolean showCities, // по умолчанию true
            @RequestParam(required = false) String lang   // ru/en
    ) {
        TileRequestDto request = TileRequestDto.builder()
                .z(z).x(x).y(y)
                .types(types)
                .severities(severities)
                .fromDate(fromDate)
                .showCities(showCities != null ? showCities : true)
                .lang(lang)
                .build();
        FeatureCollection result = tileService.getTile(request);
        return ResponseEntity.ok(result);
    }
}
