package org.example.mappro.tiles.repository;

import org.example.mappro.tiles.model.ViolationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ViolationTypeRepository extends JpaRepository<ViolationType, Long> {
    Optional<ViolationType> findByName(String name);

    Optional<ViolationType> findById(Long id);
}
