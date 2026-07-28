package org.example.mappro.tiles.tileMVT.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tileMVT.service.ViolationTileMVTService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tiles/violations")
@RequiredArgsConstructor
@Slf4j
public class ViolationTileController {

    private final ViolationTileMVTService violationTileService;

    @GetMapping("/{z}/{x}/{y}")
    public ResponseEntity<byte[]> getViolationTile(
        @PathVariable int z,
        @PathVariable int x,
        @PathVariable int y,
        @RequestParam(required = false) List<String> types,
        @RequestParam(required = false) List<String> severities,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false, defaultValue = "true") boolean showCities
    ) {
        log.info("GET /api/tiles/violations/{}/{}/{}?types={}&severities={}&fromDate={}&showCities={}", 
            z, x, y, types, severities, fromDate, showCities);

        byte[] tile = violationTileService.getViolationTile(
            z, x, y, types, severities, fromDate, showCities
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/x-protobuf")
                .body(tile);
    }
}