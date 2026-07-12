package org.example.mappro.tiles.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.dto.GeoObjectCreateDto;
import org.example.mappro.tiles.dto.ViolationCreateDto;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.model.Violation;
import org.example.mappro.tiles.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/geo-objects")
    public ResponseEntity<GeoObject> createGeoObject(@Valid @RequestBody GeoObjectCreateDto dto) {
        GeoObject saved = adminService.createGeoObject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/violations")
    public ResponseEntity<Violation> createViolation(@Valid @RequestBody ViolationCreateDto dto) {
        Violation saved = adminService.createViolation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}