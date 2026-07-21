package org.example.mappro.tiles.geoobject.controller;

import org.example.mappro.tiles.geoobject.dto.GeoObjectCreateDto;
import org.example.mappro.tiles.geoobject.dto.GeoObjectResponseDto;
import org.example.mappro.tiles.geoobject.service.GeoObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/geo-objects")
@RequiredArgsConstructor
@Tag(name = "Объект")
public class GeoObjectController {

    private final GeoObjectService geoObjectService;

    @PostMapping
    @Operation(summary = "Создание объекта")
    public ResponseEntity<GeoObjectResponseDto> createGeoObject(@RequestBody GeoObjectCreateDto dto) {
        GeoObjectResponseDto created = geoObjectService.createGeoObject(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Поиск объекта по id")
    public ResponseEntity<GeoObjectResponseDto> getGeoObjectById(@PathVariable @Parameter(description = "id объекта") Long id) {
        GeoObjectResponseDto geoObject = geoObjectService.getGeoObject(id);
        return ResponseEntity.ok(geoObject);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Поиск объекта по названию")
    public ResponseEntity<GeoObjectResponseDto> getGeoObjectByName(@RequestParam @Parameter(description = "Название объекта") String name) {
        GeoObjectResponseDto geoObject = geoObjectService.getGeoObjectByName(name);
        return ResponseEntity.ok(geoObject);
    }

    @GetMapping("/exists")
    @Operation(summary = "Проверка объекта на существование по имени")
    public ResponseEntity<Boolean> existsByName(@RequestParam @Parameter(description = "Название объекта") String name) {
        boolean exists = geoObjectService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}
