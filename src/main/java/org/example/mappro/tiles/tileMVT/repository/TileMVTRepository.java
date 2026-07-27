package org.example.mappro.tiles.tileMVT.repository;

import java.util.List;

public interface TileMVTRepository {

    byte[] getTile(
        int z,
        int x,
        int y,
        List<String> types,
        double simplifyTolerance,
        List<Long> excludedIds
    );

}