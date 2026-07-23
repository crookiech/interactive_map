package org.example.mappro.tiles.tileMVT.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.mappro.tiles.tileMVT.dto.ViolationTileMVTRequestDto;
import org.example.mappro.tiles.tileMVT.repository.ViolationTileMVTRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViolationTileMVTService {
    
    private final ViolationTileMVTRepository violationTileRepository;

    @Cacheable(value = "violationTiles", key = "#request")
    public byte[] getViolationTile(ViolationTileMVTRequestDto request) {
        log.debug("Getting violation tile for z={}, x={}, y={}", 
                request.getZ(), request.getX(), request.getY());

        return violationTileRepository.getViolationTile(
                request.getZ(),
                request.getX(),
                request.getY(),
                request.getTypes(),
                request.getSeverities(),
                request.getFromDate(),
                request.getShowCities() == null || request.getShowCities()
        );
    }  
}
