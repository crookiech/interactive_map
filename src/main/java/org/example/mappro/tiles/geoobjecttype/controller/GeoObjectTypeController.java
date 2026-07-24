package org.example.mappro.tiles.geoobjecttype.controller;

import java.util.List;

import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeCreateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypePatchDto;
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

    @GetMapping
    @Operation(summary = "Получение типов объектов")
    public ResponseEntity<List<GeoObjectTypeResponseDto>> getGeoObjectTypes() {
        return ResponseEntity.ok(geoObjectTypeService.getGeoObjectTypes());
    }

    @PostMapping
    @Operation(summary = "Создание типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> createGeoObjectType(
        @Valid @RequestBody GeoObjectTypeCreateDto dto
    ) {
        GeoObjectTypeResponseDto response = geoObjectTypeService.createGeoObjectType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частичное изменение типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> patchGeoObjectType(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypePatchDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.patchGeoObjectType(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление типа объекта")
    public ResponseEntity<Void> deleteGeoObjectType(
        @PathVariable Long id
    ) {
        geoObjectTypeService.deleteGeoObjectType(id);
        return ResponseEntity.noContent().build();
    }
}
