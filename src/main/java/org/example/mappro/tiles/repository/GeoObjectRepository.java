package org.example.mappro.tiles.repository;

import org.example.mappro.tiles.model.GeoObject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GeoObjectRepository extends JpaRepository<GeoObject, Long> {
    Optional<GeoObject> findByName(String name);

    List<GeoObject> findByType(Long type);

    List<GeoObject> findByParent(Long parent);
}
