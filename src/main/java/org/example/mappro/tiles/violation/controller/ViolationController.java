package org.example.mappro.tiles.violation.controller;

import org.example.mappro.tiles.violation.dto.ViolationCreateDto;
import org.example.mappro.tiles.violation.dto.ViolationResponseDto;
import org.example.mappro.tiles.violation.service.ViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
@Tag(name = "Инцидент")
public class ViolationController {
    
    private final ViolationService violationService;

    @PostMapping
    @Operation(summary = "Создание инцидента")
    public ResponseEntity<ViolationResponseDto> createViolation(@Valid @RequestBody ViolationCreateDto dto) {
        ViolationResponseDto response = violationService.createViolation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Поиск инцидента по id")
    public ResponseEntity<ViolationResponseDto> getViolation(@PathVariable @Parameter(description = "id инцидента") Long id) {
        return ResponseEntity.ok(violationService.getViolation(id));
    }

    @GetMapping
    @Operation(summary = "Выдача всех инцидентов")
    public ResponseEntity<List<ViolationResponseDto>> getAllViolations() {
        return ResponseEntity.ok(violationService.getAllViolations());
    }

    @GetMapping("/severity/{severity}")
    @Operation(summary = "Поиск инцидента по важности")
    public ResponseEntity<List<ViolationResponseDto>> getBySeverity(@PathVariable @Parameter(description = "Важность инцидента") String severity) {
        return ResponseEntity.ok(violationService.getBySeverity(severity));
    }
}