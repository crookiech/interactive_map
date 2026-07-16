package org.example.mappro.tiles.violation.controller;

import org.example.mappro.tiles.violation.dto.ViolationCreateDto;
import org.example.mappro.tiles.violation.dto.ViolationResponseDto;
import org.example.mappro.tiles.violation.service.ViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
public class ViolationController {
    
    private final ViolationService violationService;

    @PostMapping
    public ResponseEntity<ViolationResponseDto> createViolation(@Valid @RequestBody ViolationCreateDto dto) {
        ViolationResponseDto response = violationService.createViolation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ViolationResponseDto> getViolation(@PathVariable Long id) {
        return ResponseEntity.ok(violationService.getViolation(id));
    }

    @GetMapping
    public ResponseEntity<List<ViolationResponseDto>> getAllViolations() {
        return ResponseEntity.ok(violationService.getAllViolations());
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<ViolationResponseDto>> getBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(violationService.getBySeverity(severity));
    }
}