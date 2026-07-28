package org.example.mappro.tiles.tileMVT.repository;

import java.util.List;

public interface ViolationTileMVTAggregatedRepository {
    byte[] getViolationTile(
        int z,
        int x,
        int y,
        List<String> types
    );
}
