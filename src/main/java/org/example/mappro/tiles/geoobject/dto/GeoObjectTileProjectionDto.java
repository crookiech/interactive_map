package org.example.mappro.tiles.geoobject.dto;

public interface GeoObjectTileProjectionDto {
    Long getId();
    String getName();
    String getType();
    String getGeometryJson();
    Long getParentId();
    Integer getLabelPriority();
    Boolean getIsSegment();
    Integer getSegmentOrder();
    Long getViolationCount();
    String[] getViolationTypes();
}
