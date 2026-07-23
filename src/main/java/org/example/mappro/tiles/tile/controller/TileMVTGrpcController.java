package org.example.mappro.tiles.tile.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tileMVT.dto.TileRequestDto;
import org.example.mappro.tiles.tileMVT.service.TileMVTService;
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

    private final TileMVTService tileService;

    @GetMapping(value = "/{z}/{x}/{y}", produces = "application/x-protobuf")
    public ResponseEntity<byte[]> getTile(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<String> severities,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) Boolean showCities,
            @RequestParam(required = false) String lang
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

// package org.example.mappro.tiles.tile.controller;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.example.mappro.tiles.tile.service.TileRESTService;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;

// @RestController
// @RequestMapping("/api/tiles")
// @RequiredArgsConstructor
// @Slf4j
// @Tag(name = "Тайл", description = "Стандарт: XYZ (Slippy Map)")
// public class TileController {

//     private final TileRESTService tileService;

//     @GetMapping("/{z}/{x}/{y}")
//     @Operation(summary = "Поиск объекта по тайлу")
//     public String getTile(
//             @PathVariable @Parameter(description = "Уровень масштабирования") int z,
//             @PathVariable @Parameter(description = "Горизонтальная координата тайла") int x,
//             @PathVariable @Parameter(description = "Вертикальная координата тайла") int y) {

//         return tileService.getTileAsJson(
//                 org.example.mappro.tiles.tile.dto.TileRequestDto.builder()
//                         .z(z).x(x).y(y).build()
//         );
//     }
// }