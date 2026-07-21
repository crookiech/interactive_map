package org.example.mappro.tiles.violationtype.controller;

import org.example.mappro.tiles.violationtype.dto.ViolationTypeCreateDto;
import org.example.mappro.tiles.violationtype.dto.ViolationTypeResponseDto;
import org.example.mappro.tiles.violationtype.service.ViolationTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/violation-types")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Тип инцидента")
public class ViolationTypeController {

    private final ViolationTypeService violationTypeService;

    @PostMapping
    @Operation(summary = "Создание типа инцидента")
    public ResponseEntity<ViolationTypeResponseDto> createGeoObjectType(@Valid @RequestBody ViolationTypeCreateDto dto) {
        ViolationTypeResponseDto response = violationTypeService.createViolationType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
