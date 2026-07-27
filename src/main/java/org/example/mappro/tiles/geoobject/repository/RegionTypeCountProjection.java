package org.example.mappro.tiles.geoobject.repository;

public interface RegionTypeCountProjection {
    Long getRegionId();

    String getRegionName();

    Long getTypeId();

    String getTypeCode();

    String getTypeName();

    Long getObjectCount();
}
