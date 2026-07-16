package org.example.mappro.tiles.geoobjecttype.controller;

import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeCreateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeResponseDto;
import org.example.mappro.tiles.geoobjecttype.service.GeoObjectTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/geo-object-types")
@Slf4j
@RequiredArgsConstructor
public class GeoObjectTypeController {

    private final GeoObjectTypeService geoObjectTypeService;

    @PostMapping
    public ResponseEntity<GeoObjectTypeResponseDto> createGeoObjectType(@Valid @RequestBody GeoObjectTypeCreateDto dto) {
        GeoObjectTypeResponseDto response = geoObjectTypeService.createGeoObjectType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
