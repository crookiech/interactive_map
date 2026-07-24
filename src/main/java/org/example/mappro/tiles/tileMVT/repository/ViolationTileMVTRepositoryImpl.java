package org.example.mappro.tiles.tileMVT.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
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
        StringBuilder sql = buildViolationQuery();
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("z", z)
                .addValue("x", x)
                .addValue("y", y);

        appendWhereConditions(sql, params, types, severities, fromDate, showCities);

        sql.append("""
            GROUP BY
                go.id,
                go.name,
                got.code,
                v.id,
                v.date,
                v.severity,
                v.status,
                v.type_id,
                vt.name,
                go.geometry
        )
        SELECT ST_AsMVT(
            tile,
            'violations',
            4096,
            'geom'
        )
        FROM tile
        """);

        log.debug("Executing violation tile query for z={}, x={}, y={}", z, x, y);

        try {
            byte[] tile = jdbcTemplate.queryForObject(
                    sql.toString(),
                    params,
                    byte[].class
            );
            return tile == null ? new byte[0] : tile;
        } catch (Exception e) {
            log.error("Error executing violation tile query: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    private StringBuilder buildViolationQuery() {
        return new StringBuilder("""
        WITH tile AS (
            SELECT
                go.id AS "geoObjectId",
                go.name AS "geoObjectName",
                got.code AS "geoObjectType",
                v.id AS "violationId",
                v.date AS "violationDate",
                v.severity AS "violationSeverity",
                v.status AS "violationStatus",
                v.type_id AS "violationTypeId",
                vt.name AS "violationTypeName",
                ST_AsMVTGeom(
                    ST_Transform(go.geometry, 3857),
                    ST_TileEnvelope(:z, :x, :y),
                    4096,
                    64,
                    true
                ) AS geom
            FROM geo_objects go
            JOIN geo_object_types got ON got.id = go.type_id
            INNER JOIN geo_object_violation gov ON gov.object_id = go.id
            INNER JOIN violations v ON v.id = gov.violation_id
            LEFT JOIN violation_types vt ON vt.id = v.type_id
            WHERE
                ST_Intersects(
                    ST_Transform(go.geometry, 3857),
                    ST_TileEnvelope(:z, :x, :y)
                )
                AND got.lod_min <= :z
                AND got.lod_max >= :z
        """);
    }

    private void appendWhereConditions(
            StringBuilder sql,
            MapSqlParameterSource params,
            List<String> types,
            List<String> severities,
            LocalDate fromDate,
            boolean showCities
    ) {
        if (!showCities) {
            sql.append("""
                AND got.code <> 'CITY'
        """);
        }

        if (types != null && !types.isEmpty()) {
            sql.append("""
                AND got.code = ANY(CAST(:types AS text[]))
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
    }
}
