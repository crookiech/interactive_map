package org.example.mappro.tiles.geoobject.controller;

import org.example.mappro.tiles.geoobject.dto.GeoObjectCreateDto;
import org.example.mappro.tiles.geoobject.dto.GeoObjectResponseDto;
import org.example.mappro.tiles.geoobject.service.GeoObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geo-objects")
@RequiredArgsConstructor
public class GeoObjectController {

    private final GeoObjectService geoObjectService;

    @PostMapping
    public ResponseEntity<GeoObjectResponseDto> createGeoObject(@RequestBody GeoObjectCreateDto dto) {
        GeoObjectResponseDto created = geoObjectService.createGeoObject(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<GeoObjectResponseDto> getGeoObjectById(@PathVariable Long id) {
        GeoObjectResponseDto geoObject = geoObjectService.getGeoObject(id);
        return ResponseEntity.ok(geoObject);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<GeoObjectResponseDto> getGeoObjectByName(@RequestParam String name) {
        GeoObjectResponseDto geoObject = geoObjectService.getGeoObjectByName(name);
        return ResponseEntity.ok(geoObject);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
        boolean exists = geoObjectService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}
