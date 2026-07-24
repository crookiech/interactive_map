package org.example.mappro.tiles.city.service;

import lombok.RequiredArgsConstructor;
import org.example.mappro.tiles.city.repository.CityTileRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityTileService {
    private final CityTileRepository cityTileRepository;

    @Cacheable(value = "cityTiles", key = "{#z, #x, #y}")
    public byte[] getTile(int z, int x, int y) {
        return cityTileRepository.getTile(z, x, y);
    }
}
