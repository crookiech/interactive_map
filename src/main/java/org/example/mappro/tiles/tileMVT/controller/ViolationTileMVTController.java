package org.example.mappro.tiles.tileMVT.controller;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.mappro.tiles.tileMVT.dto.ViolationTileMVTRequestDto;
import org.example.mappro.tiles.tileMVT.service.ViolationTileMVTService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tiles/mvt")
@RequiredArgsConstructor
@Slf4j
public class ViolationTileMVTController {
    private final ViolationTileMVTService violationTileMVTService;

    @GetMapping("/violation/{z}/{x}/{y}")
    public ResponseEntity<byte[]> getViolationTile(
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
        log.debug("Received violation tile request: z={}, x={}, y={}, types={}, severities={}, fromDate={}", 
                z, x, y, types, severities, fromDate);

        ViolationTileMVTRequestDto request = ViolationTileMVTRequestDto.builder()
                .z(z)
                .x(x)
                .y(y)
                .types(types)
                .severities(severities)
                .fromDate(fromDate)
                .showCities(showCities == null || showCities)
                .lang(lang)
                .build();

        byte[] tile = violationTileMVTService.getViolationTile(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/x-protobuf")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .body(tile);
    }
}
