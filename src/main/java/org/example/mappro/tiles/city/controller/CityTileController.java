package org.example.mappro.tiles.city.controller;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.city.service.CityTileService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tiles/mvt/cities")
@RequiredArgsConstructor
public class CityTileController {
    private final CityTileService cityTileService;

    @GetMapping(value = "/{z}/{x}/{y}", produces = "application/x-protobuf")
    public ResponseEntity<byte[]> getTile(
        @PathVariable int z,
        @PathVariable int x,
        @PathVariable int y
    ) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/x-protobuf")
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
            .body(cityTileService.getTile(z, x, y));
    }
}
