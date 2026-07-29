package org.example.mappro.tiles.tileMVT.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ViolationTileMVTAggregatedRepositoryImpl implements ViolationTileMVTAggregatedRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public byte[] getViolationTile(
        int z,
        int x,
        int y,
        List<String> types
    ) {
        log.info("Getting aggregated violation MVT tile: z={}, x={}, y={}, types={}", z, x, y, types);

        try {
            String sql = """
                WITH RECURSIVE parent_tree AS (
                    SELECT 
                        go.id,
                        go.parent_id,
                        go.geometry,
                        go.name,
                        got.code AS type_code
                    FROM geo_objects go
                    JOIN geo_object_types got ON got.id = go.type_id
                    WHERE 
                        ST_Intersects(
                            ST_Transform(go.geometry, 3857),
                            ST_TileEnvelope(:z, :x, :y)
                        )
                        AND got.lod_min <= :z
                        AND got.lod_max >= :z
                ),
                region_geometry AS (
                    SELECT DISTINCT
                        pt.name AS region_name,
                        ST_Centroid(ST_Transform(pt.geometry, 3857)) AS region_center
                    FROM parent_tree pt
                    WHERE pt.type_code = 'REGION'
                ),
                violations_in_tile AS (
                    SELECT 
                        v.id,
                        vt.code AS violation_type_code,
                        vt.display_name AS violation_type_name,
                        go.id AS object_id,
                        go.geometry,
                        go.parent_id
                    FROM violations v
                    JOIN geo_object_violation gov ON gov.violation_id = v.id
                    JOIN geo_objects go ON go.id = gov.object_id
                    LEFT JOIN violation_types vt ON vt.id = v.type_id
                    WHERE 
                        ST_Intersects(
                            ST_Transform(go.geometry, 3857),
                            ST_TileEnvelope(:z, :x, :y)
                        )
                        AND go.geometry IS NOT NULL
                ),
                violations_with_region AS (
                    SELECT 
                        vit.violation_type_code,
                        vit.violation_type_name,
                        vit.id,
                        vit.geometry,
                        COALESCE(
                            (
                                SELECT pt.name
                                FROM parent_tree pt
                                WHERE (pt.id = vit.object_id OR pt.id = vit.parent_id)
                                  AND pt.type_code = 'REGION'
                                LIMIT 1
                            ),
                            'Unknown'
                        ) AS region_name
                    FROM violations_in_tile vit
                ),
                aggregated AS (
                    SELECT 
                        vwr.region_name,
                        vwr.violation_type_code,
                        vwr.violation_type_name,
                        COUNT(DISTINCT vwr.id) AS count
                    FROM violations_with_region vwr
                    WHERE 1=1
                """;

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("z", z)
                    .addValue("x", x)
                    .addValue("y", y);

            if (types != null && !types.isEmpty()) {
                sql += """
                        AND vwr.violation_type_code = ANY(CAST(:types AS text[]))
                    """;
                params.addValue("types", types.toArray(new String[0]));
            }

            sql += """
                    GROUP BY vwr.region_name, vwr.violation_type_code, vwr.violation_type_name
                ),
                violations_by_region AS (
                    SELECT 
                        region_name,
                        jsonb_agg(
                            jsonb_build_object(
                                'type', violation_type_code,
                                'typeName', violation_type_name,
                                'count', count
                            )
                            ORDER BY count DESC
                        ) AS violations
                    FROM aggregated
                    GROUP BY region_name
                )
                SELECT 
                    vbr.region_name AS region,
                    vbr.violations AS types,
                    ST_AsMVTGeom(
                        rg.region_center,
                        ST_TileEnvelope(:z, :x, :y),
                        4096,
                        64,
                        true
                    ) AS geom
                FROM violations_by_region vbr
                LEFT JOIN region_geometry rg ON rg.region_name = vbr.region_name
                WHERE rg.region_center IS NOT NULL
                  AND ST_Intersects(rg.region_center, ST_TileEnvelope(:z, :x, :y))
                """;

            String mvtSql = """
                WITH tile_data AS (
                    %s
                )
                SELECT ST_AsMVT(tile_data, 'violations_aggregated', 4096, 'geom')
                FROM tile_data
                WHERE geom IS NOT NULL
                """.formatted(sql);

            log.debug("Executing aggregated MVT query for z={}, x={}, y={}", z, x, y);
            
            byte[] tile = jdbcTemplate.queryForObject(
                    mvtSql,
                    params,
                    byte[].class
            );

            log.info("Generated aggregated MVT tile size: {} bytes", tile != null ? tile.length : 0);
            return tile == null ? new byte[0] : tile;
            
        } catch (Exception e) {
            log.error("Error getting aggregated violation MVT tile: {}", e.getMessage(), e);
            return new byte[0];
        }
    }
}