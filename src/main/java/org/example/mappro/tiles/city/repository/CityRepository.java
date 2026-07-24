package org.example.mappro.tiles.city.repository;

import java.util.Set;
import org.example.mappro.tiles.city.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CityRepository extends JpaRepository<City, Long> {
    @Query("select city.externalId from City city")
    Set<String> findAllExternalIds();
}
