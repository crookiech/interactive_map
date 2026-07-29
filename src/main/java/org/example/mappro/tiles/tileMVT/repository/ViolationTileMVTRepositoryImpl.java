package org.example.mappro.tiles.tileMVT.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ViolationTileMVTRepositoryImpl implements ViolationTileMVTRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public byte[] getViolationTile(
        int z,
        int x,
        int y,
        List<String> types,
        List<String> severities,
        LocalDate fromDate,
        boolean showCities
    ) {
        log.info("Getting detailed violation MVT tile: z={}, x={}, y={}", z, x, y);

        try {

            String sql = """
                WITH filtered_violations AS (
                    SELECT
                        v.id AS violation_id,
                        vt.code AS violation_type_code,
                        vt.display_name AS violation_type_name,
                        go.id AS object_id
                FROM violations v
                JOIN geo_object_violation gov ON gov.violation_id = v.id
                JOIN geo_objects go ON go.id = gov.object_id
                LEFT JOIN violation_types vt ON vt.id = v.type_id
                JOIN geo_object_types got ON got.id = go.type_id
                WHERE 
                    ST_Intersects(
                        ST_Transform(go.geometry, 3857),
                        ST_TileEnvelope(:z, :x, :y)
                    )
                    AND go.geometry IS NOT NULL
                    AND got.lod_min <= :z
                    AND got.lod_max >= :z
                """;

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("z", z)
                    .addValue("x", x)
                    .addValue("y", y);

            if (types != null && !types.isEmpty()) {
                sql += """
                        AND vt.code = ANY(CAST(:types AS text[]))
                    """;
                params.addValue("types", types.toArray(new String[0]));
            }

            if (severities != null && !severities.isEmpty()) {
                sql += """
                        AND v.severity = ANY(CAST(:severities AS text[]))
                    """;
                params.addValue("severities", severities.toArray(new String[0]));
            }

            if (fromDate != null) {
                sql += """
                        AND v.date >= :fromDate
                    """;
                params.addValue("fromDate", fromDate.atStartOfDay());
            }

            if (!showCities) {
                sql += """
                        AND got.code != 'CITY'
                    """;
            }

            sql += """
                ),
                violations_by_type AS (
                    SELECT
                        object_id,
                        violation_type_code,
                        violation_type_name,
                        COUNT(DISTINCT violation_id) AS count
                    FROM filtered_violations
                    GROUP BY object_id, violation_type_code, violation_type_name
                ),
                violations_by_object AS (
                    SELECT
                        object_id,
                        SUM(count)::bigint AS violation_count,
                        jsonb_agg(
                            jsonb_build_object(
                                'type', violation_type_code,
                                'typeName', violation_type_name,
                                'count', count
                            )
                            ORDER BY count DESC, violation_type_code
                        ) AS violations
                    FROM violations_by_type
                    GROUP BY object_id
                )
                SELECT
                    vbo.object_id AS id,
                    vbo.object_id,
                    go.name AS object_name,
                    vbo.violation_count AS "violationCount",
                    vbo.violations::text AS types,
                    ST_AsMVTGeom(
                        ST_Transform(go.geometry, 3857),
                        ST_TileEnvelope(:z, :x, :y),
                        4096,
                        64,
                        true
                    ) AS geom
                FROM violations_by_object vbo
                JOIN geo_objects go ON go.id = vbo.object_id
                """;

            String mvtSql = """
                WITH tile_data AS (
                    %s
                )
                SELECT ST_AsMVT(tile_data, 'violations', 4096, 'geom')
                FROM tile_data
                WHERE geom IS NOT NULL
                """.formatted(sql);

            log.debug("Executing detailed MVT query for z={}, x={}, y={}", z, x, y);
            
            byte[] tile = jdbcTemplate.queryForObject(
                    mvtSql,
                    params,
                    byte[].class
            );

            log.info("Generated detailed MVT tile size: {} bytes", tile != null ? tile.length : 0);
            return tile == null ? new byte[0] : tile;
            
        } catch (Exception e) {
            log.error("Error getting detailed violation MVT tile: {}", e.getMessage(), e);
            return new byte[0];
        }
    }
}
