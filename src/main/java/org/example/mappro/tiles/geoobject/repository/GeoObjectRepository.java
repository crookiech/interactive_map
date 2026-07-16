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
        SELECT go.id, go.name, go.type,
            ST_AsGeoJSON(ST_Simplify(go.geometry, :simplifyTolerance, true)) AS geometryJson,
            go.parent_id AS parentId, go.label_priority AS labelPriority, go.is_segment AS isSegment,
            go.segment_order AS segmentOrder, COUNT(v.id) AS violationCount, ARRAY_AGG(DISTINCT v.type) AS violationTypes
        FROM geo_objects go
        LEFT JOIN violations v ON v.geo_object_id = go.id
        WHERE
            ST_Intersects(ST_Transform(go.geometry, 4326), ST_Transform(ST_MakeEnvelope(:xmin, :ymin, :xmax, :ymax, 3857), 4326))
            AND go.lod_min <= :z AND go.lod_max >= :z
            AND (:showCities = TRUE OR go.type != 'CITY')
            AND (COALESCE(CAST(:types AS text[]), '{}'::text[]) = '{}'::text[] OR go.type = ANY(CAST(:types AS text[])))
            AND (COALESCE(CAST(:severities AS text[]), '{}'::text[]) = '{}'::text[] OR v.severity = ANY(CAST(:severities AS text[])))
            AND (:fromDate IS NULL OR v.date >= :fromDate)
            AND (:returnSegments = FALSE OR go.is_segment = TRUE)
        GROUP BY
            go.id, go.geometry, go.parent_id, go.label_priority,
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
            @Param("simplifyTolerance") double simplifyTolerance,
            @Param("returnSegments") boolean returnSegments
    );
    List<GeoObject> findAllByType_NameOrderByIdAsc(String typeName);
}
