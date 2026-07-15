package org.example.mappro.tiles.controller;

import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.service.GeoObjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geo_objects")
@RequiredArgsConstructor
public class GeoObjectController {

    private final GeoObjectService service;

    // Объект по id
    @GetMapping("/{id}")
    public ResponseEntity<GeoObject> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Объект по имени
    @GetMapping("/name/{name}")
    public ResponseEntity<GeoObject> getByName(@PathVariable String name) {
        return ResponseEntity.ok(service.getByName(name));
    }
}