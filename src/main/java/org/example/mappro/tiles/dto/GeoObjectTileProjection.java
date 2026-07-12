package org.example.mappro.tiles.dto;

/**
 * Проекция для результатов запроса к geo_objects.
 * Имена методов должны совпадать с алиасами столбцов в SQL.
 */
public interface GeoObjectTileProjection {
    Long getId();
    String getName();
    String getType();
    String getGeometryJson();      // ST_AsGeoJSON(...)
    Long getParentId();
    Integer getLabelPriority();
    Boolean getIsSegment();
    Integer getSegmentOrder();
    Long getViolationCount();
    String[] getViolationTypes();  // ARRAY_AGG
}