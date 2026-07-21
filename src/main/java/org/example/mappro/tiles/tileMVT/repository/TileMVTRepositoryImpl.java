package org.example.mappro.tiles.tileMVT.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
            List<String> severities,
            LocalDate fromDate,
            boolean showCities,
            double simplifyTolerance
    ) {

        StringBuilder sql = new StringBuilder("""
        WITH tile AS (

            SELECT
                go.id,
                go.name,
                got.name AS type,

                go.parent_id      AS "parentId",
                go.label_priority AS "labelPriority",
                go.is_segment     AS "isSegment",
                go.segment_order  AS "segmentOrder",

                COUNT(v.id) AS "violationCount",

                ARRAY_REMOVE(
                    ARRAY_AGG(DISTINCT vt.name),
                    NULL
                ) AS "violationTypes",

                ST_AsMVTGeom(
                    ST_Transform(go.geometry,3857),
                    ST_TileEnvelope(:z,:x,:y),
                    4096,
                    64,
                    true
                ) AS geom

            FROM geo_objects go

            JOIN geo_object_types got
                ON got.id = go.type_id

            LEFT JOIN geo_object_violation gov
                ON gov.object_id = go.id

            LEFT JOIN violations v
                ON v.id = gov.violation_id

            LEFT JOIN violation_types vt
                ON vt.id = v.type_id

            WHERE

                ST_Intersects(
                    ST_Transform(go.geometry,3857),
                    ST_TileEnvelope(:z,:x,:y)
                )

                AND go.lod_min <= :z
                AND go.lod_max >= :z
        """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("z", z)
                .addValue("x", x)
                .addValue("y", y);

        if (!showCities) {
            sql.append("""
                AND got.name <> 'CITY'
        """);
        }

        if (types != null && !types.isEmpty()) {
            sql.append("""
                AND got.name = ANY(CAST(:types AS text[]))
        """);
            params.addValue("types", types.toArray(new String[0]));
        }

        if (severities != null && !severities.isEmpty()) {
            sql.append("""
                AND v.severity = ANY(CAST(:severities AS text[]))
        """);
            params.addValue("severities", severities.toArray(new String[0]));
        }

        if (fromDate != null) {
            sql.append("""
                AND v.date >= :fromDate
        """);
            params.addValue("fromDate", fromDate);
        }

        sql.append("""
            GROUP BY
                go.id,
                go.name,
                got.name,
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