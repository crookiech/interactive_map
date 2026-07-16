package org.example.mappro.tiles.importer.controller;

import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.importer.dto.KmzImportResponse;
import org.example.mappro.tiles.importer.service.KmzImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/import")
@RequiredArgsConstructor
public class KmzImportController {
    private final KmzImportService importService;

    @PostMapping(value = "/kmz", consumes = "multipart/form-data")
    public ResponseEntity<KmzImportResponse> importKmz(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(importService.importFile(file));
    }
}
