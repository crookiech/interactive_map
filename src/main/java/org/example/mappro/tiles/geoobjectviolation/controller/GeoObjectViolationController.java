package org.example.mappro.tiles.geoobjectviolation.controller;

import org.example.mappro.tiles.geoobjectviolation.dto.GeoObjectViolationCreateDto;
import org.example.mappro.tiles.geoobjectviolation.dto.GeoObjectViolationResponseDto;
import org.example.mappro.tiles.geoobjectviolation.service.GeoObjectViolationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/geo-object-violations")
@RequiredArgsConstructor
@Tag(name = "Связь между объектом и инцидентом")
public class GeoObjectViolationController {
    private final GeoObjectViolationService geoObjectViolationService;

    @PostMapping
    @Operation(summary = "Создание связи между объектом и инцидентом")
    public ResponseEntity<GeoObjectViolationResponseDto> createGeoObjectViolation(@Valid @RequestBody GeoObjectViolationCreateDto dto) {
        GeoObjectViolationResponseDto response = geoObjectViolationService.createGeoObjectViolation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск связи по id")
    public ResponseEntity<GeoObjectViolationResponseDto> getGeoObjectViolation(@PathVariable @Parameter(description = "id связи") Long id) {
        return ResponseEntity.ok(geoObjectViolationService.getGeoObjectViolation(id));
    }

    @GetMapping
    @Operation(summary = "Выдача всех связей")
    public ResponseEntity<List<GeoObjectViolationResponseDto>> getAllGeoObjectViolations() {
        return ResponseEntity.ok(geoObjectViolationService.getAllGeoObjectViolations());
    }

    @GetMapping("/object/{objectId}")
    @Operation(summary = "Поиск связи по id объекта")
    public ResponseEntity<List<GeoObjectViolationResponseDto>> getViolationsByObject(@PathVariable @Parameter(description = "id объекта") Long objectId) {
        return ResponseEntity.ok(geoObjectViolationService.getViolationsByObjectId(objectId));
    }

    @GetMapping("/violation/{violationId}")
    @Operation(summary = "Поиск связи по id инцидента")
    public ResponseEntity<List<GeoObjectViolationResponseDto>> getObjectsByViolation(@PathVariable @Parameter(description = "id инцидента") Long violationId) {
        return ResponseEntity.ok(geoObjectViolationService.getObjectsByViolationId(violationId));
    }

    @GetMapping("/exists")
    @Operation(summary = "Проверка связи на существование")
    public ResponseEntity<Boolean> existsRelation(
            @RequestParam @Parameter(description = "id объекта") Long objectId,
            @RequestParam @Parameter(description = "id инцидента") Long violationId) {
        return ResponseEntity.ok(geoObjectViolationService.existsRelation(objectId, violationId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление связи по id")
    public ResponseEntity<Void> deleteGeoObjectViolation(@PathVariable @Parameter(description = "id связи") Long id) {
        geoObjectViolationService.deleteGeoObjectViolation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Удаление связи по id объекта и инцидента")
    public ResponseEntity<Void> deleteGeoObjectViolationByObjectAndViolation(
            @RequestParam @Parameter(description = "id объекта") Long objectId,
            @RequestParam @Parameter(description = "id инцидента") Long violationId) {
        geoObjectViolationService.deleteGeoObjectViolationByObjectAndViolation(objectId, violationId);
        return ResponseEntity.noContent().build();
    }
}
