package org.example.mappro.tiles.tileMVT.repository;

import java.time.LocalDate;
import java.util.List;

public interface TileMVTRepository {

    byte[] getTile(
            int z,
            int x,
            int y,
            List<String> types,
            List<String> severities,
            LocalDate fromDate,
            boolean showCities,
            double simplifyTolerance
    );

}