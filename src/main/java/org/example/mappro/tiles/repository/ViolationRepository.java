package org.example.mappro.tiles.repository;

import org.example.mappro.tiles.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViolationRepository extends JpaRepository<Violation, Long> {
}