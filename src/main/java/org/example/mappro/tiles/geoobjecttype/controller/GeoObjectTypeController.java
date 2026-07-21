package org.example.mappro.tiles.geoobjecttype.controller;

import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeCreateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeResponseDto;
import org.example.mappro.tiles.geoobjecttype.service.GeoObjectTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/geo-object-types")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Тип объекта")
public class GeoObjectTypeController {

    private final GeoObjectTypeService geoObjectTypeService;

    @PostMapping
    @Operation(summary = "Создание типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> createGeoObjectType(@Valid @RequestBody GeoObjectTypeCreateDto dto) {
        GeoObjectTypeResponseDto response = geoObjectTypeService.createGeoObjectType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
