package org.example.mappro.tiles.geoobject.repository;

import java.util.List;

public interface GeoObjectRepositoryCustom {
    List<Long> findDescendantIdsByGeometry(Long parentId);
}
