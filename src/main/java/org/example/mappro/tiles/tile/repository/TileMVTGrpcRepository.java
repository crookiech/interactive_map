package org.example.mappro.tiles.tile.repository;

import java.util.List;

public interface TileMVTGrpcRepository {
    byte[] getTile(
        int z,
        int x,
        int y,
        List<String> types,
        double simplifyTolerance
    );
}
