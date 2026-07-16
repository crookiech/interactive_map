// package org.example.mappro.tiles.violation.controller;

// import org.example.mappro.tiles.violation.model.Violation;
// import org.example.mappro.tiles.geoobject.dto.GeoObjectCreateDto;
// import org.example.mappro.tiles.geoobject.dto.GeoObjectResponseDto;
// import org.example.mappro.tiles.violation.dto.ViolationCreateDto;
// import org.example.mappro.tiles.violation.service.ViolationService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/violations")
// @RequiredArgsConstructor
// public class ViolationController {
    
//     private final ViolationService violationService;

//     @PostMapping
//     public ResponseEntity<ViolationCreateDto> createViolation(@RequestBody ViolationCreateDto dto) {
//         ViolationCreateDto created = violationService.createViolation(dto);
//         return new ResponseEntity<>(created, HttpStatus.CREATED);
//     }
// }