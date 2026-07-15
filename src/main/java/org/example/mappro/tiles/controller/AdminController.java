package org.example.mappro.tiles.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.dto.GeoObjectCreateDto;
import org.example.mappro.tiles.dto.GeoObjectResponseDto;
import org.example.mappro.tiles.dto.ViolationCreateDto;
import org.example.mappro.tiles.dto.ViolationResponseDto;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.model.GeoObjectType;
import org.example.mappro.tiles.model.Violation;
import org.example.mappro.tiles.model.ViolationType;
import org.example.mappro.tiles.service.AdminService;
import org.example.mappro.tiles.service.DtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final DtoMapper dtoMapper;

    @PostMapping("/geo-objects")
    public ResponseEntity<GeoObjectResponseDto> createGeoObject(@Valid @RequestBody GeoObjectCreateDto dto) {
        GeoObject saved = adminService.createGeoObject(dto);
        GeoObjectResponseDto response = dtoMapper.toGeoObjectResponseDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/geo-object-types")
    public ResponseEntity<GeoObjectType> createGeoObjectType(@Valid @RequestBody GeoObjectType dto) {
        GeoObjectType created = adminService.createGeoObjectType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/violations")
    public ResponseEntity<ViolationResponseDto> createViolation(@Valid @RequestBody ViolationCreateDto dto) {
        Violation saved = adminService.createViolation(dto);
        ViolationResponseDto response = dtoMapper.toViolationResponseDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/violation-types")
    public ResponseEntity<ViolationType> createViolationType(@Valid @RequestBody ViolationType dto) {
        ViolationType created = adminService.createViolationType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
