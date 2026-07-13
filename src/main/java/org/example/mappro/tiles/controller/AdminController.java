package org.example.mappro.tiles.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.dto.GeoObjectCreateDto;
import org.example.mappro.tiles.dto.GeoObjectResponseDto;
import org.example.mappro.tiles.dto.ViolationCreateDto;
import org.example.mappro.tiles.dto.ViolationResponseDto;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.model.Violation;
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
    private final DtoMapper dtoMapper;  // Добавляем маппер

    @PostMapping("/geo-objects")
    public ResponseEntity<GeoObjectResponseDto> createGeoObject(@Valid @RequestBody GeoObjectCreateDto dto) {
        // Сохраняем в БД
        GeoObject saved = adminService.createGeoObject(dto);
        
        // Конвертируем в DTO для ответа
        GeoObjectResponseDto response = dtoMapper.toGeoObjectResponseDto(saved);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/violations")
    public ResponseEntity<ViolationResponseDto> createViolation(@Valid @RequestBody ViolationCreateDto dto) {
        // Сохраняем в БД
        Violation saved = adminService.createViolation(dto);
        
        // Конвертируем в DTO для ответа
        ViolationResponseDto response = dtoMapper.toViolationResponseDto(saved);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
