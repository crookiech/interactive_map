package org.example.mappro.tiles.geoobjectviolation.repository;

import org.example.mappro.tiles.geoobjectviolation.model.GeoObjectViolation;
import org.example.mappro.tiles.geoobject.model.GeoObject;
import org.example.mappro.tiles.violation.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeoObjectViolationRepository extends JpaRepository<GeoObjectViolation, Long> {

    // id нарушений для объекта
    @Query("""
        SELECT gov.violation FROM GeoObjectViolation gov
        WHERE gov.geoObject.id = :objectId
    """)
    List<Violation> findViolationsByObjectId(@Param("objectId") Long objectId);

    // id объектов для нарушения
    @Query("""
        SELECT gov.geoObject FROM GeoObjectViolation gov 
        WHERE gov.violation.id = :violationId
    """)
    List<GeoObject> findObjectsByViolationId(@Param("violationId") Long violationId);

    // Проверить, есть ли связь
    boolean existsByGeoObjectIdAndViolationId(Long objectId, Long violationId);

    // Удалить связь
    void deleteByGeoObjectIdAndViolationId(Long objectId, Long violationId);
}