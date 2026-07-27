package org.example.mappro.tiles.tile.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TileMVTGrpcRepositoryImpl implements TileMVTGrpcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public byte[] getTile(
            int z,
            int x,
            int y,
            List<String> types,
            double simplifyTolerance
    ) {

        StringBuilder sql = new StringBuilder("""
        WITH tile AS (
            SELECT
                go.id,
                go.name,
                got.code AS type,
                got.color_hex AS color,
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
            LEFT JOIN geo_object_violation gov ON gov.object_id = go.id
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

        sql.append("""
            GROUP BY
                go.id,
                go.name,
                got.code,
                got.color_hex,
                go.geometry,
                go.parent_id,
                go.label_priority,
                go.is_segment,
                go.segment_order
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
