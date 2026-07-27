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
public class TileMVTRepositoryImpl implements TileMVTRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public byte[] getTile(
        int z,
        int x,
        int y,
        List<String> types,
        double simplifyTolerance,
        List<Long> regionIds
    ) {
        log.debug("Repository: z={}, x={}, y={}, regionIds={}", z, x, y, regionIds);

        StringBuilder sql = new StringBuilder("""
        WITH tile AS (
            SELECT
                go.id,
                go.name,
                got.code AS type,
                go.parent_id      AS "parentId",
                go.label_priority AS "labelPriority",
                go.is_segment     AS "isSegment",
                go.segment_order  AS "segmentOrder",
                ST_AsMVTGeom(
                    ST_Transform(go.geometry, 3857),
                    ST_TileEnvelope(:z, :x, :y),
                    4096,
                    64,
                    true
                ) AS geom
            FROM geo_objects go
            JOIN geo_object_types got ON got.id = go.type_id
            WHERE
                ST_Intersects(
                    ST_Transform(go.geometry, 3857),
                    ST_TileEnvelope(:z, :x, :y)
                )
                AND got.lod_min <= :z
                AND got.lod_max >= :z
        """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("z", z)
                .addValue("x", x)
                .addValue("y", y);

        if (types != null && !types.isEmpty()) {
            sql.append("""
                AND got.code = ANY(CAST(:types AS text[]))
        """);
            params.addValue("types", types.toArray(new String[0]));
        }

        if (regionIds != null && !regionIds.isEmpty()) {
            List<Long> filteredRegionIds = regionIds.stream()
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .toList();

            if (!filteredRegionIds.isEmpty()) {
                sql.append("""
                    AND EXISTS (
                        SELECT 1
                        FROM geo_objects region
                        JOIN geo_object_types region_type ON region_type.id = region.type_id
                        WHERE region.id IN (:regionIds)
                          AND region_type.code = 'REGION'
                          AND (
                              (
                                  GeometryType(go.geometry) IN ('LINESTRING', 'MULTILINESTRING')
                                  AND ST_Intersects(go.geometry, region.geometry)
                              )
                              OR (
                                  GeometryType(go.geometry) NOT IN ('LINESTRING', 'MULTILINESTRING')
                                  AND ST_CoveredBy(go.geometry, region.geometry)
                              )
                          )
                    )
            """);
                params.addValue("regionIds", filteredRegionIds);
            }
        }

        sql.append("""
        )
        SELECT ST_AsMVT(
            tile,
            'geo_objects',
            4096,
            'geom'
        )
        FROM tile
        """);

        byte[] tile = jdbcTemplate.queryForObject(
            sql.toString(),
            params,
            byte[].class
        );

        return tile == null ? new byte[0] : tile;
    }
}
