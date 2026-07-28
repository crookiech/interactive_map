package org.example.mappro.tiles.geoobject.repository;

import org.example.mappro.tiles.geoobject.model.GeoObject;
import org.example.mappro.tiles.geoobject.dto.GeoObjectTileProjectionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GeoObjectRepository extends JpaRepository<GeoObject, Long> {
    Optional<GeoObject> findByName(String name);

    List<GeoObject> findByType(Long type);

    List<GeoObject> findByParent(Long parent);

    @Query(value = """
        SELECT
            region.id AS "regionId",
            region.name AS "regionName",
            grouped.type_id AS "typeId",
            grouped.type_code AS "typeCode",
            grouped.type_name AS "typeName",
            grouped.object_count AS "objectCount"
        FROM geo_objects region
        JOIN geo_object_types region_type ON region_type.id = region.type_id
        LEFT JOIN LATERAL (
            SELECT
                object_type.id AS type_id,
                object_type.code AS type_code,
                object_type.display_name AS type_name,
                COUNT(*) AS object_count,
                object_type.sort_order AS type_sort_order
                FROM geo_objects object
                JOIN geo_object_types object_type ON object_type.id = object.type_id
                WHERE object_type.code <> 'REGION'
                  AND (
                      (
                          GeometryType(object.geometry) IN ('LINESTRING', 'MULTILINESTRING')
                          AND ST_Intersects(object.geometry, region.geometry)
                      )
                      OR (
                          GeometryType(object.geometry) NOT IN ('LINESTRING', 'MULTILINESTRING')
                          AND ST_CoveredBy(object.geometry, region.geometry)
                      )
                  )
                GROUP BY object_type.id, object_type.code, object_type.display_name, object_type.sort_order
        ) grouped ON TRUE
        WHERE region_type.code = 'REGION'
        ORDER BY LOWER(region.name), region.id, grouped.type_sort_order, grouped.type_code
    """, nativeQuery = true)
    List<RegionTypeCountProjection> findRegionTypeCounts();

    @Query(value = """
        SELECT
            object.id AS id,
            object.name AS name,
            object_type.code AS "typeCode"
        FROM geo_objects region
        JOIN geo_object_types region_type ON region_type.id = region.type_id
        JOIN geo_objects object ON (
            (
                GeometryType(object.geometry) IN ('LINESTRING', 'MULTILINESTRING')
                AND ST_Intersects(object.geometry, region.geometry)
            )
            OR (
                GeometryType(object.geometry) NOT IN ('LINESTRING', 'MULTILINESTRING')
                AND ST_CoveredBy(object.geometry, region.geometry)
            )
        )
        JOIN geo_object_types object_type ON object_type.id = object.type_id
        WHERE region.id = :regionId
          AND region_type.code = 'REGION'
          AND object_type.id = :typeId
          AND object_type.code <> 'REGION'
        ORDER BY LOWER(object.name), object.id
    """, nativeQuery = true)
    List<RegionObjectProjection> findObjectsByRegionIdAndTypeId(
            @Param("regionId") Long regionId,
            @Param("typeId") Long typeId
    );

    @Query(value = """
        SELECT go.id, go.name, got.name AS type,
            ST_AsGeoJSON(
                ST_Transform(
                    ST_SimplifyPreserveTopology(
                        ST_Transform(go.geometry, 3857),
                        :simplifyTolerance
                    ),
                    4326
                )
            ) AS geometryJson,
            go.parent_id AS parentId, go.label_priority AS labelPriority, go.is_segment AS isSegment,
            go.segment_order AS segmentOrder, COUNT(v.id) AS violationCount,
            ARRAY_REMOVE(ARRAY_AGG(DISTINCT vt.code), NULL) AS violationTypes
        FROM geo_objects go
        JOIN geo_object_types got ON got.id = go.type_id
        LEFT JOIN geo_object_violation gov ON gov.object_id = go.id
        LEFT JOIN violations v ON v.id = gov.violation_id
        LEFT JOIN violation_types vt ON vt.id = v.type_id
        WHERE
            ST_Intersects(go.geometry, ST_Transform(ST_MakeEnvelope(:xmin, :ymin, :xmax, :ymax, 3857), 4326))
            AND got.lod_min <= :z AND got.lod_max >= :z
            AND (:showCities = TRUE OR got.name != 'CITY')
            AND (COALESCE(CAST(:types AS text[]), '{}'::text[]) = '{}'::text[] OR got.name = ANY(CAST(:types AS text[])))
            AND (COALESCE(CAST(:severities AS text[]), '{}'::text[]) = '{}'::text[] OR v.severity = ANY(CAST(:severities AS text[])))
            AND (:fromDate IS NULL OR v.date >= :fromDate)
        GROUP BY
            go.id, got.name, go.geometry, go.parent_id, go.label_priority,
            go.is_segment, go.segment_order
        LIMIT 2000
    """, nativeQuery = true)
    List<GeoObjectTileProjectionDto> findObjectsForTile(
            @Param("z") int z,
            @Param("xmin") double xmin,
            @Param("ymin") double ymin,
            @Param("xmax") double xmax,
            @Param("ymax") double ymax,
            @Param("types") List<String> types,
            @Param("severities") List<String> severities,
            @Param("fromDate") LocalDate fromDate,
            @Param("showCities") boolean showCities,
            @Param("simplifyTolerance") double simplifyTolerance
    );

}
