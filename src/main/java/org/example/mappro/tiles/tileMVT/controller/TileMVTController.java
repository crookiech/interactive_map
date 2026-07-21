package org.example.mappro.tiles.tileMVT.controller;

import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.tileMVT.dto.TileRequestDto;
import org.example.mappro.tiles.tileMVT.service.TileMVTService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tiles/mvt")
@RequiredArgsConstructor
public class TileMVTController {

    private final TileMVTService tileService;

    @GetMapping("/{z}/{x}/{y}")
    public ResponseEntity<byte[]> getTile(

            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,

            @RequestParam(required = false)
            List<String> types,

            @RequestParam(required = false)
            List<String> severities,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            Boolean showCities,

            @RequestParam(required = false)
            String lang

    ) {

        TileRequestDto request = TileRequestDto.builder()
                .z(z)
                .x(x)
                .y(y)
                .types(types)
                .severities(severities)
                .fromDate(fromDate)
                .showCities(showCities == null || showCities)
                .lang(lang)
                .build();

        byte[] tile = tileService.getTile(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/x-protobuf")
                .body(tile);
    }
}