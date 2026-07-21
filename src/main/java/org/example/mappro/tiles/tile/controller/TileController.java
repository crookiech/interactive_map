package org.example.mappro.tiles.tile.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tile.service.TileRESTService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tiles")
@RequiredArgsConstructor
@Slf4j
public class TileController {

    private final TileRESTService tileService;
    
    @GetMapping("/{z}/{x}/{y}")
    public String getTile(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y) {
        
        return tileService.getTileAsJson(
            org.example.mappro.tiles.tile.dto.TileRequestDto.builder()
                .z(z).x(x).y(y).build()
        );
    }
}