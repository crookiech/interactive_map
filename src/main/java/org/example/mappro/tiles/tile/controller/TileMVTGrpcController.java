package org.example.mappro.tiles.tile.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tile.dto.TileRequestDto;
import org.example.mappro.tiles.tile.service.TileMVTGrpcService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tiles")
@RequiredArgsConstructor
@Slf4j
public class TileMVTGrpcController {

    private final TileMVTGrpcService tileMVTGrpcService;

    @GetMapping(value = "/{z}/{x}/{y}", produces = "application/x-protobuf")
    public ResponseEntity<byte[]> getTile(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) String lang
    ) {
        TileRequestDto request = TileRequestDto.builder()
                .z(z)
                .x(x)
                .y(y)
                .types(types)
                .lang(lang)
                .build();

        byte[] tile = tileMVTGrpcService.getTile(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/x-protobuf")
                .body(tile);
    }
}
