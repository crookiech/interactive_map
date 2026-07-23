package org.example.mappro.tiles.tile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tileMVT.repository.TileMVTRepository;
import org.example.mappro.tiles.tile.dto.TileRequestDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TileMVTGrpcService {

    private final TileMVTRepository tileRepository;

    public byte[] getTileAsMVT(TileRequestDto request) {
        log.info("Получение MVT тайла: z={}, x={}, y={}", 
                request.getZ(), request.getX(), request.getY());

        // Используем существующий репозиторий из MVT модуля
        return tileRepository.getTile(
                request.getZ(),
                request.getX(),
                request.getY(),
                request.getTypes(),
                request.getSeverities(),
                request.getFromDate(),
                request.getShowCities() != null && request.getShowCities(),
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