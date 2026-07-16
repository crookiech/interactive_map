package org.example.mappro.tiles.geoobjectviolation.controller;

import org.example.mappro.tiles.geoobjectviolation.dto.GeoObjectViolationCreateDto;
import org.example.mappro.tiles.geoobjectviolation.dto.GeoObjectViolationResponseDto;
import org.example.mappro.tiles.geoobjectviolation.service.GeoObjectViolationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/geo-object-violations")
@RequiredArgsConstructor
public class GeoObjectViolationController {
    private final GeoObjectViolationService geoObjectViolationService;

    @PostMapping
    public ResponseEntity<GeoObjectViolationResponseDto> createGeoObjectViolation(
            @Valid @RequestBody GeoObjectViolationCreateDto dto) {
        GeoObjectViolationResponseDto response = geoObjectViolationService.createGeoObjectViolation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeoObjectViolationResponseDto> getGeoObjectViolation(@PathVariable Long id) {
        return ResponseEntity.ok(geoObjectViolationService.getGeoObjectViolation(id));
    }

    @GetMapping
    public ResponseEntity<List<GeoObjectViolationResponseDto>> getAllGeoObjectViolations() {
        return ResponseEntity.ok(geoObjectViolationService.getAllGeoObjectViolations());
    }

    @GetMapping("/object/{objectId}")
    public ResponseEntity<List<GeoObjectViolationResponseDto>> getViolationsByObject(@PathVariable Long objectId) {
        return ResponseEntity.ok(geoObjectViolationService.getViolationsByObjectId(objectId));
    }

    @GetMapping("/violation/{violationId}")
    public ResponseEntity<List<GeoObjectViolationResponseDto>> getObjectsByViolation(@PathVariable Long violationId) {
        return ResponseEntity.ok(geoObjectViolationService.getObjectsByViolationId(violationId));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsRelation(
            @RequestParam Long objectId,
            @RequestParam Long violationId) {
        return ResponseEntity.ok(geoObjectViolationService.existsRelation(objectId, violationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeoObjectViolation(@PathVariable Long id) {
        geoObjectViolationService.deleteGeoObjectViolation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteGeoObjectViolationByObjectAndViolation(
            @RequestParam Long objectId,
            @RequestParam Long violationId) {
        geoObjectViolationService.deleteGeoObjectViolationByObjectAndViolation(objectId, violationId);
        return ResponseEntity.noContent().build();
    }
}
