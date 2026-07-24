package org.example.mappro.tiles.city.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CityTileRepositoryImpl implements CityTileRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public byte[] getTile(int z, int x, int y) {
        String sql = """
            WITH tile AS (
                SELECT city.id, city.name, city.population, city.label_priority,
                    ST_AsMVTGeom(
                        ST_Transform(city.geometry, 3857),
                        ST_TileEnvelope(:z, :x, :y), 4096, 64, true
                    ) AS geom
                FROM cities city
                WHERE city.visible = true
                  AND city.label_priority >= CASE
                      WHEN :z <= 3 THEN 7
                      WHEN :z = 4 THEN 6
                      WHEN :z = 5 THEN 5
                      WHEN :z = 6 THEN 4
                      ELSE 1
                  END
                  AND ST_Intersects(
                      ST_Transform(city.geometry, 3857),
                      ST_TileEnvelope(:z, :x, :y)
                  )
            )
            SELECT ST_AsMVT(tile, 'cities', 4096, 'geom') FROM tile
            """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("z", z).addValue("x", x).addValue("y", y);
        byte[] tile = jdbcTemplate.queryForObject(sql, parameters, byte[].class);
        return tile == null ? new byte[0] : tile;
    }
}
