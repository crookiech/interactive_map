package org.example.mappro.tiles.repository;

import org.example.mappro.tiles.model.GeoObjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GeoObjectTypeRepository extends JpaRepository<GeoObjectType, Long> {
    Optional<GeoObjectType> findByName(String name);

    Optional<GeoObjectType> findById(Long id);
}
