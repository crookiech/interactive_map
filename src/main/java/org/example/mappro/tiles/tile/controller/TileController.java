package org.example.mappro.tiles.tile.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tile.service.TileRESTService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tiles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Тайл", description = "Стандарт: XYZ (Slippy Map)")
public class TileController {

    private final TileRESTService tileService;

    @GetMapping("/{z}/{x}/{y}")
    @Operation(summary = "Поиск объекта по тайлу")
    public String getTile(
            @PathVariable @Parameter(description = "Уровень масштабирования") int z,
            @PathVariable @Parameter(description = "Горизонтальная координата тайла") int x,
            @PathVariable @Parameter(description = "Вертикальная координата тайла") int y) {

        return tileService.getTileAsJson(
                org.example.mappro.tiles.tile.dto.TileRequestDto.builder()
                        .z(z).x(x).y(y).build()
        );
    }
}