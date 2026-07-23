package org.example.mappro.tiles.tile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tile.dto.TileRequestDto;
import org.example.mappro.tiles.tile.repository.TileMVTGrpcRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TileMVTGrpcService {

    private final TileMVTGrpcRepository tileMVTGrpcRepository;

    @Cacheable(value = "tiles", key = "#request")
    public byte[] getTile(TileRequestDto request) {

        return tileMVTGrpcRepository.getTile(
            request.getZ(),
            request.getX(),
            request.getY(),
            request.getTypes(),
            computeSimplifyTolerance(request.getZ())
        );
    }

    public byte[] getTileAsMVT(TileRequestDto request) {
        log.info("Получение MVT тайла: z={}, x={}, y={}", request.getZ(), request.getX(), request.getY());

        return tileMVTGrpcRepository.getTile(
            request.getZ(),
            request.getX(),
            request.getY(),
            request.getTypes(),
            computeSimplifyTolerance(request.getZ())
        );
    }

    private double computeSimplifyTolerance(int z) {
        if (z <= 5) return 5000.0;
        else if (z <= 10) return 1000.0;
        else if (z <= 15) return 100.0;
        else return 0.0;
    }
}