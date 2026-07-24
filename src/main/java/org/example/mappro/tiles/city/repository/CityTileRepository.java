package org.example.mappro.tiles.city.repository;

public interface CityTileRepository {
    byte[] getTile(int z, int x, int y);
}
