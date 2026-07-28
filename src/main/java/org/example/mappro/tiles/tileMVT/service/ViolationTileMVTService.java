package org.example.mappro.tiles.tileMVT.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.tileMVT.repository.ViolationTileMVTAggregatedRepository;
import org.example.mappro.tiles.tileMVT.repository.ViolationTileMVTRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViolationTileMVTService {

    private final ViolationTileMVTAggregatedRepository aggregatedRepository;
    private final ViolationTileMVTRepository violationTileRepository;

    public byte[] getViolationTile(
        int z,
        int x,
        int y,
        List<String> types,
        List<String> severities,
        LocalDate fromDate,
        boolean showCities
    ) {
        log.debug("Getting violation tile for zoom: {}", z);
        
        if (z < 5) {
            log.debug("Using aggregated repository for zoom level {}", z);
            return aggregatedRepository.getViolationTile(z, x, y, types);
        } else {
            log.debug("Using detailed repository for zoom level {}", z);
            return violationTileRepository.getViolationTile(
                z, x, y, types, severities, fromDate, showCities
            );
        }
    }
}