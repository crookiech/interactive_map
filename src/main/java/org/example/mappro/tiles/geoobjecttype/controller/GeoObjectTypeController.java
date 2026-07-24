package org.example.mappro.tiles.geoobjecttype.controller;

import java.util.List;

import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeCodeUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeColorUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeCreateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeDisplayNameUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeGeometryUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeIconUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeLodUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypePatchDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeResponseDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeSortOrderUpdateDto;
import org.example.mappro.tiles.geoobjecttype.dto.GeoObjectTypeVisibilityUpdateDto;
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

    @PutMapping("/{id}/lod")
    @Operation(summary = "Изменение уровней детализации типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> updateGeoObjectTypeLod(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypeLodUpdateDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.updateLod(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частичное изменение типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> patchGeoObjectType(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypePatchDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.patchGeoObjectType(id, dto));
    }

    @PutMapping("/{id}/code")
    @Operation(summary = "Изменение кода типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> updateGeoObjectTypeCode(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypeCodeUpdateDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.updateCode(id, dto));
    }

    @PutMapping("/{id}/display-name")
    @Operation(summary = "Изменение отображаемого имени типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> updateGeoObjectTypeDisplayName(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypeDisplayNameUpdateDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.updateDisplayName(id, dto));
    }

    @PutMapping("/{id}/geometry-type")
    @Operation(summary = "Изменение типа геометрии объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> updateGeoObjectTypeGeometry(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypeGeometryUpdateDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.updateGeometryType(id, dto));
    }

    @PutMapping("/{id}/color")
    @Operation(summary = "Изменение цвета типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> updateGeoObjectTypeColor(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypeColorUpdateDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.updateColor(id, dto));
    }

    @PutMapping("/{id}/icon")
    @Operation(summary = "Изменение иконки типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> updateGeoObjectTypeIcon(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypeIconUpdateDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.updateIcon(id, dto));
    }

    @PutMapping("/{id}/sort-order")
    @Operation(summary = "Изменение порядка сортировки типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> updateGeoObjectTypeSortOrder(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypeSortOrderUpdateDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.updateSortOrder(id, dto));
    }

    @PutMapping("/{id}/visibility")
    @Operation(summary = "Изменение видимости по умолчанию типа объекта")
    public ResponseEntity<GeoObjectTypeResponseDto> updateGeoObjectTypeVisibility(
        @PathVariable Long id,
        @Valid @RequestBody GeoObjectTypeVisibilityUpdateDto dto
    ) {
        return ResponseEntity.ok(geoObjectTypeService.updateVisibility(id, dto));
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
