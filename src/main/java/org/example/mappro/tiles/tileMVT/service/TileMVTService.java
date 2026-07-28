package org.example.mappro.tiles.tileMVT.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tileMVT.dto.TileRequestDto;
import org.example.mappro.tiles.tileMVT.repository.TileMVTRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TileMVTService {

    private final TileMVTRepository tileRepository;

    @Cacheable(value = "tiles", key = "#request")
    public byte[] getTile(TileRequestDto request) {

        return tileRepository.getTile(
            request.getZ(),
            request.getX(),
            request.getY(),
            request.getTypes(),
            computeSimplifyTolerance(request.getZ()),
            request.getRegionIds()
        );
    }

    private double computeSimplifyTolerance(int z) {
        if (z <= 5) return 5000.0;
        else if (z <= 10) return 1000.0;
        else if (z <= 15) return 100.0;
        else return 0.0;
    }
}
